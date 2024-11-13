package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.TransactionResponseDto;
import swp.koi.model.Transaction;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionEntityToDtoConverter {

    private final ModelMapper modelMapper;
    private final MemberEntityToDtoConverter memberEntityToDtoConverter;

    public List<TransactionResponseDto> convertTransactionList(List<Transaction> transactions){
        List<TransactionResponseDto> response = transactions.stream().map(transaction -> {
            TransactionResponseDto dto = modelMapper.map(transaction, TransactionResponseDto.class);

            if (transaction.getLot() != null || transaction.getMember() != null) {

                Integer lotId = transaction.getLot().getLotId();
                dto.setLotId(lotId);

                dto.setMember(memberEntityToDtoConverter.convertMember(transaction.getMember()));

            } else {
                dto.setLotId(null);
                dto.setMember(null);
            }


            return dto;
        }).collect(Collectors.toList());

        return response;
    }

}
