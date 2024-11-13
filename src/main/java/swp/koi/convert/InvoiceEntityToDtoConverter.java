package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.InvoiceResponseDto;
import swp.koi.model.Invoice;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceEntityToDtoConverter {

    private final ModelMapper modelMapper;
    private final MemberEntityToDtoConverter memberEntityToDtoConverter;

    public List<InvoiceResponseDto> convertInvoiceList(List<Invoice> invoiceList){
        List<InvoiceResponseDto> response = invoiceList.stream()
                .map(invoice -> {
                    InvoiceResponseDto dto = modelMapper.map(invoice, InvoiceResponseDto.class);

                    dto.getKoiFish().setBreederName(invoice.getKoiFish().getAuctionRequest().getKoiBreeder().getBreederName());
                    dto.getKoiFish().setVarietyName(invoice.getKoiFish().getVariety().getVarietyName());
                    dto.getKoiFish().setImageUrl(invoice.getKoiFish().getMedia().getImageUrl());
                    dto.getKoiFish().setVideoUrl(invoice.getKoiFish().getMedia().getVideoUrl());
                    dto.setMember(memberEntityToDtoConverter.convertMember(invoice.getMember()));
                    return dto;
                })
                .collect(Collectors.toList());
        return response;
    }

    public InvoiceResponseDto convertInvoiceDto(Invoice invoice){
        InvoiceResponseDto dto = modelMapper.map(invoice, InvoiceResponseDto.class);

        dto.getKoiFish().setBreederName(invoice.getKoiFish().getAuctionRequest().getKoiBreeder().getBreederName());
        dto.getKoiFish().setVarietyName(invoice.getKoiFish().getVariety().getVarietyName());
        dto.getKoiFish().setImageUrl(invoice.getKoiFish().getMedia().getImageUrl());
        dto.getKoiFish().setVideoUrl(invoice.getKoiFish().getMedia().getVideoUrl());
        return dto;
    }

}
