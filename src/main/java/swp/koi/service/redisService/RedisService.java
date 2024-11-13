package swp.koi.service.redisService;

import swp.koi.model.SubscribeRequest;

import java.util.List;
import java.util.Set;

public interface RedisService {
    void saveData(String key, Object value, Long expireTime);

    void saveDataWithoutTime(String key, Object value);

    void saveDataToSet(String key, Object object);

    Object getData(String key);

    List<?> getListData(String key);

    Set<?> getSetData(String key);

    void deleteData(String key);

    void deleteDataFromSet(String key, Object object);

    boolean existData(String key);

    void saveListData(String key, List<?> list);

    boolean isUserFollowedThisLot(int lotId);

    void unfollowLot(int lotId, SubscribeRequest token);
}
