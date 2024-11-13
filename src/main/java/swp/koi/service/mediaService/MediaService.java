package swp.koi.service.mediaService;

import swp.koi.dto.request.MediaDto;
import swp.koi.model.Media;

public interface MediaService {
    Media createMediaFromRequest(MediaDto mediaDTO);

    Media findByMediaId(Integer mediaId);

    void save(Media media);

}
