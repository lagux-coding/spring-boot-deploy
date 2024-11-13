package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import swp.koi.dto.response.AccountFullResponseDto;
import swp.koi.dto.response.AccountResponseDto;
import swp.koi.model.Account;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class AccountEntityToDtoConverter {

    private final ModelMapper modelMapper;

    public AccountResponseDto convertAccount(Account account){
        AccountResponseDto response = modelMapper.map(account, AccountResponseDto.class);
        return response;
    }

    public List<AccountResponseDto> convertAccountList(List<Account> accountList){
        List<AccountResponseDto> response = accountList
                .stream()
                .map(account -> modelMapper.map(account, AccountResponseDto.class))
                .collect(Collectors.toList());
        return response;
    }

    public List<AccountFullResponseDto> convertAccountFullList(List<Account> accountList){
        List<AccountFullResponseDto> response = accountList
                .stream()
                .map(account -> modelMapper.map(account, AccountFullResponseDto.class))
                .collect(Collectors.toList());
        return response;
    }

}
