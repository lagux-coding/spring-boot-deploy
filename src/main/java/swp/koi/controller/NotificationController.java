package swp.koi.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.koi.dto.request.SubscribeRequestDto;
import swp.koi.model.Member;
import swp.koi.model.SubscribeRequest;
import swp.koi.service.authService.GetUserInfoByUsingAuth;
import swp.koi.service.redisService.RedisServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "notification", description = "Everything about your notification")
public class NotificationController {

    private final RedisServiceImpl redisService;
    private final GetUserInfoByUsingAuth authService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> saveFcmToken(@RequestBody SubscribeRequestDto subscribeRequestDTO){
        try {
            if (subscribeRequestDTO == null) {
                return ResponseEntity.badRequest().body("Invalid request body");
            }
            Member member = authService.getMemberFromAuth();
            SubscribeRequest subInfo = SubscribeRequest.builder()
                    .token(subscribeRequestDTO.getToken())
                    .memberId(member.getMemberId())
                    .build();
            redisService.saveDataToSet("Notify_"+subscribeRequestDTO.getLotId(), subInfo);
            return ResponseEntity.ok("Token saved successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/user-subcribed-yet")
    public ResponseEntity<?> isUserSubscribed(@RequestParam int lotId){
        boolean userRegistered = redisService.isUserFollowedThisLot(lotId);
        if (userRegistered) {
            return ResponseEntity.ok("User subscribed");
        }
        return ResponseEntity.badRequest().body("User not subscribed");
    }

    @GetMapping("/unfollow-lot")
    public ResponseEntity<?> removeUserFromNotify(@RequestParam int lotId,
                                                  @RequestParam String token){
        Member member = authService.getMemberFromAuth();
        SubscribeRequest subInfo = SubscribeRequest.builder()
                .token(token)
                .memberId(member.getMemberId())
                .build();
        redisService.unfollowLot(lotId, subInfo);
        return ResponseEntity.ok().body("Successfully unfollowed lot for user");
    }
}
