package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.LotRegisterForMemberResponseDto;
import swp.koi.dto.response.LotRegisterResponseDto;
import swp.koi.dto.response.LotResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.LotRegister;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LotRegisterEntityToDtoConverter {

    private final ModelMapper modelMapper;
    private final LotEntityToDtoConverter lotEntityToDtoConverter;

    public LotRegisterResponseDto convertLotRegister(LotRegister lotRegister){
        if(lotRegister == null)
            throw new KoiException(ResponseCode.FOUND_NOTHING);
        LotRegisterResponseDto response = modelMapper.map(lotRegister, LotRegisterResponseDto.class);
        return response;
    }

    public List<LotRegisterForMemberResponseDto> convertLotRegisterListForMember(List<LotRegister> allDepositedLotForMember) {
        List<LotRegisterForMemberResponseDto> response = allDepositedLotForMember.stream()
                .map(lotRegister -> {
                    LotRegisterForMemberResponseDto dto = modelMapper.map(lotRegister, LotRegisterForMemberResponseDto.class);

                    LotResponseDto lotDto = lotEntityToDtoConverter.convertLot(lotRegister.getLot());
                    lotDto.getKoiFish().setImageUrl(lotRegister.getLot().getKoiFish().getMedia().getImageUrl());
                    lotDto.getKoiFish().setVideoUrl(lotRegister.getLot().getKoiFish().getMedia().getVideoUrl());
                    lotDto.getKoiFish().setBreederName(lotRegister.getLot().getKoiFish().getAuctionRequest().getKoiBreeder().getBreederName());

                    dto.setLot(lotDto);
                    dto.setAuctionId(lotRegister.getLot().getAuction().getAuctionId());

                    return dto;
                }).collect(Collectors.toList());
        return response;
    }
}
