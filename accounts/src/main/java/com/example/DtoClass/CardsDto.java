package com.example.DtoClass;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
        name = "Cards",
        description = "Schema to hold Cards info"
)
public class CardsDto {

    @Schema(

            description = "Mobile Number of the Customer"
    )
    private String mobileNumber;
    @Schema
            (
                    description = "Card Number of the Card"
            )
    private String cardNumber;
    @Schema(

            description = "Card type is a card type",
            example = "Credit Card"
    )
    private String cardType;
    @Schema(

            description = "total limit is card limit",
            example = "100000"
    )
    private int totalLimit;
    @Schema(
            description = "Amount is card is used"
    )
    private int amountUsed;
    @Schema(

            description = "Amount available in card "
    )
    private int availableAmount;
}
