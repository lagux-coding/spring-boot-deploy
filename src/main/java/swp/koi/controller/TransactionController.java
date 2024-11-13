package swp.koi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.convert.TransactionEntityToDtoConverter;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.dto.response.TransactionResponseDto;
import swp.koi.service.transactionService.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Tag(name = "transaction", description = "Everything about your transaction")
public class TransactionController {

    private final TransactionEntityToDtoConverter transactionEntityToDtoConverter;
    private final TransactionService transactionService;

    @GetMapping("/get-all-transaction")
    public ResponseData<?> getAllTransaction(){
        List<TransactionResponseDto> response = transactionEntityToDtoConverter.convertTransactionList(transactionService.getAllTransaction());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

}
