package swp.koi.service.mailService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailContent {

    private final TemplateEngine templateEngine;

    public String createEmailSignUpGoogle(String firstName, String email, String password) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("email", email);
        context.setVariable("password", password);
        return templateEngine.process("signupEmail", context);
    }

    public String createEmailResetPassword(String firstName, String resetLink){
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("resetLink", resetLink);
        return templateEngine.process("resetPassword", context);
    }

    public String createEmailChangedPassword(String firstName){
        Context context = new Context();
        context.setVariable("firstName", firstName);
        return templateEngine.process("changedPassword", context);
    }
}