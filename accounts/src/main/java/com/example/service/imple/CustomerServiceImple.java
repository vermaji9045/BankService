package com.example.service.imple;

import com.example.DtoClass.AccountsDto;
import com.example.DtoClass.CardsDto;
import com.example.DtoClass.CustomerDetailsDto;
import com.example.DtoClass.LoansDto;
import com.example.Entity.Accounts;
import com.example.Entity.Customer;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.AccountsMapper;
import com.example.Mapper.CustomerMapper;
import com.example.Repository.AccountsRepository;
import com.example.Repository.CustomerRepository;
import com.example.service.ICustomerService;
import com.example.service.client.CardsFeignClient;
import com.example.service.client.LoansFeingClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImple implements ICustomerService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;

    private CardsFeignClient cardsFeignClient;

    private LoansFeingClient loansFeingClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber,String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Accounts", "CustomerId", customer.getCustomerId().toString())
        );
        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeingClient.fetchLoanDetails(correlationId, mobileNumber);

        if (null != loansDtoResponseEntity) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }


        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (null != cardsDtoResponseEntity)
        {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }
        return customerDetailsDto;
    }
}
