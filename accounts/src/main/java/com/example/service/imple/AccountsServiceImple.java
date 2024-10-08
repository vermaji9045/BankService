package com.example.service.imple;


import com.example.DtoClass.AccountMsgDto;
import com.example.DtoClass.AccountsDto;
import com.example.DtoClass.CustomerDto;
import com.example.Entity.Accounts;
import com.example.Entity.Customer;
import com.example.Exception.CustomerExist;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.AccountsMapper;
import com.example.Mapper.CustomerMapper;
import com.example.Repository.AccountsRepository;
import com.example.Repository.CustomerRepository;
import com.example.constants.AccountsConstants;
import com.example.service.IAccountsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor

public class AccountsServiceImple implements IAccountsService {

    private  static final Logger log= LoggerFactory.getLogger(AccountsServiceImple.class);

    AccountsRepository accountsRepository;
    CustomerRepository customerRepository;
    StreamBridge streamBridge;


    @Override
    public void createAccount(CustomerDto customerDto) {

        Customer customer= CustomerMapper.mapToCustomer(customerDto,new Customer());
       Optional<Customer>optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());

       if(optionalCustomer.isPresent())
       {
           throw new CustomerExist("Customer already registered with the given number"  + customerDto.getMobileNumber());
       }

      Customer savedCustomer=  customerRepository.save(customer);
     Accounts accounts= accountsRepository.save(createNewAccount(savedCustomer));

       sendCommunication(accounts,savedCustomer);
    }




    private Accounts createNewAccount(Customer customer)
    {
        Accounts newAccount=new Accounts();

        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber=1000000000L+new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);

        return newAccount;

    }

    @Override
    public CustomerDto FetchAccount(String mobileNumber) {

        Customer customer=customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()->new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );

        Accounts accounts=accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()->new ResourceNotFoundException("Accounts","CustomerId",customer.getCustomerId().toString())
        );

        CustomerDto customerDto=CustomerMapper.mapToCustomerDto(customer,new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {


        boolean isUpdated=false;

        AccountsDto accountsDto=customerDto.getAccountsDto();
        if(accountsDto !=null)
        {
            Accounts accounts=accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(

                    ()-> new ResourceNotFoundException("Account","AccountNumber",accountsDto.getAccountNumber().toString())
            );

            AccountsMapper.mapToAccounts(accountsDto,accounts);
            accounts=accountsRepository.save(accounts);

            Long customerId=accounts.getCustomerId();

            Customer customer=customerRepository.findById(customerId).orElseThrow(

                    ()->new ResourceNotFoundException("Customer","CustomerId",customerId.toString())
            );

            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated=true;
        }

        return true;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {

        Customer customer=customerRepository.findByMobileNumber(mobileNumber).orElseThrow(

                ()->new ResourceNotFoundException("Customer","MobileNumber",mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdate=false;

        if(accountNumber!=null)
        {
            Accounts accounts=accountsRepository.findById(accountNumber).orElseThrow(

                    ()-> new ResourceNotFoundException("Account","AccountNumber",accountNumber.toString())
            );

            accounts.setCommunicationSw(true);
            accountsRepository.save(accounts);
            isUpdate=true;
        }
        return isUpdate;
    }


    private void sendCommunication(Accounts accounts,Customer customer)
    {
        var accountsMsgDto=new AccountMsgDto(accounts.getAccountNumber(), customer.getName(),
                customer.getEmail(), customer.getMobileNumber());

        log.info("Sending culmination request for the details {}",accountsMsgDto);
        var result= streamBridge.send("sendCommunication-out-0",accountsMsgDto);
        log.info("Is the communication request successfully processed?:{}",result);

    }
}
