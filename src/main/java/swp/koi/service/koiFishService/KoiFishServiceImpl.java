package swp.koi.service.koiFishService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import swp.koi.convert.KoiFishEntityToDtoConverter;
import swp.koi.dto.request.*;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.AuctionRequestStatusEnum;
import swp.koi.model.enums.KoiFishStatusEnum;
import swp.koi.repository.AuctionRequestRepository;
import swp.koi.repository.KoiFishRepository;
import swp.koi.service.auctionTypeService.AuctionTypeService;
import swp.koi.service.mediaService.MediaService;
import swp.koi.service.varietyService.VarietyService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class KoiFishServiceImpl implements KoiFishService{

    private final KoiFishRepository koiFishRepository;
    private final VarietyService varietyService;
    private final MediaService mediaService;
    private final AuctionRequestRepository auctionRequestRepository;
    private final KoiFishEntityToDtoConverter koiFishEntityToDtoConverter;
    private final AuctionTypeService auctionTypeService;
    private final ModelMapper modelMapper;

    @Override
    public KoiFish createKoiFishFromRequest(KoiFishDto koiRequest, MediaDto mediaRequest) throws KoiException{
            try{
                KoiFish koiFish = new KoiFish();

                // Set the variety of the koi fish by fetching it from the varietyService based on the variety name provided
                Variety variety = varietyService.findByVarietyName(koiRequest.getVarietyName());
                koiFish.setVariety(variety);

                // Create and set the media (e.g., images or videos) related to the koi fish using the mediaService
                Media media = mediaService.createMediaFromRequest(mediaRequest);
                koiFish.setMedia(media);

                AuctionType auctionType = auctionTypeService.findByAuctionTypeName(koiRequest.getAuctionTypeName());

                // Set the age, gender, price, and size from the request DTO
                koiFish.setAge(koiRequest.getAge());
                koiFish.setGender(koiRequest.getGender());
                koiFish.setAuctionType(auctionType);
                koiFish.setPrice(koiRequest.getPrice());
                koiFish.setSize(koiRequest.getSize());

                // Set the status of the koi fish to 'PENDING' (probably waiting for auction or approval)
                koiFish.setStatus(KoiFishStatusEnum.PENDING);

                // Save the koi fish to the repository and return the saved entity
                return koiFishRepository.save(koiFish);
            }catch (KoiException e){
                throw e;
            }
    }

    @Override
    public KoiFish findByFishId(Integer fishId) {
        return koiFishRepository.findByFishId(fishId).orElseThrow(() -> new KoiException(ResponseCode.FISH_NOT_FOUND));
    }

    @Override
    public List<KoiFish> getKoiFishFromRegisteredRequest() {
        List<AuctionRequest> auctionRequestList = auctionRequestRepository.findAllByStatus(AuctionRequestStatusEnum.REGISTERED);

        List<KoiFish> koiFishList = auctionRequestList.stream()
                .map(AuctionRequest::getKoiFish)
                .filter(koiFish -> koiFish.getStatus().equals(KoiFishStatusEnum.WAITING))
                .collect(Collectors.toList());

        if(koiFishList.isEmpty())
            throw new KoiException(ResponseCode.FOUND_NOTHING);

        return koiFishList;
    }

    @Override
    public void saveFish(KoiFish koiFish) {
        koiFishRepository.save(koiFish);
    }

    @Override
    public List<KoiFish> getKoiFishBasedOnType(AuctionTypeDto auctionTypeDTO) {
        List<KoiFish> list = koiFishRepository.findAll().stream()
                .filter(fish -> fish.getAuctionType().getAuctionTypeName().equals(auctionTypeDTO.getAuctionTypeName()) && fish.getStatus().equals(KoiFishStatusEnum.WAITING))
                .collect(Collectors.toList());
        return list;
    }
}
