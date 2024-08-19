package com.publicbank.Message.functions;

import com.publicbank.Message.Dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageFunction {

    private static final Logger log= LoggerFactory.getLogger(MessageFunction.class);

    @Bean
    public Function<AccountsMsgDto,AccountsMsgDto>email(){

        return accountsMsgDto->{

            log.info("Sending email with the details:"+ accountsMsgDto.toString());
            return accountsMsgDto;
        };
    }

    @Bean
    public Function<AccountsMsgDto,Long>sms(){

        return accountsMsgDto->{

            log.info("Sending sms with the details:"+ accountsMsgDto.toString());
            return accountsMsgDto.accountNumber();
        };
    }
}
