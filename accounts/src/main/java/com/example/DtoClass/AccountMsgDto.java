package com.example.DtoClass;

public record AccountMsgDto(
        Long accountNumber, String name,String email, String mobileNumber
) {
}
