package swp.koi.service.lotRegisterService;

import swp.koi.dto.request.LotRegisterDto;
import swp.koi.dto.response.LotRegisterResponseDto;
import swp.koi.exception.KoiException;
import swp.koi.model.LotRegister;
import swp.koi.model.enums.LotRegisterStatusEnum;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface LotRegisterService {
    String regisSlotWithLotId(LotRegisterDto lotRegisterDTO) throws UnsupportedEncodingException, KoiException;

    List<LotRegisterResponseDto> listLotRegistersByLotId(int lotId);

    LotRegister getLotWinner(Integer lotId);

    boolean isRegistered(Integer lotId, Integer accountId);

    List<LotRegisterResponseDto> findAllLotRegisWithStatus(LotRegisterStatusEnum status);

    List<LotRegister> getAllDepositedLotForMember(Integer accountId);

    void refundForMember(int lotRegisterId);
}
