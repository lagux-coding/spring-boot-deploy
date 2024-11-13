package swp.koi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(KoiException.class)
    public ResponseEntity<ResponseData<?>> handleKoiException(KoiException ex) {
        int code = ex.getResponseCode().getCode();  // Get the code
        String message = ex.getResponseCode().getMessage();  // Get the error message

        ResponseData<?> responseData = new ResponseData<>(code, message);
        return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);  // Return ResponseEntity with 404 status
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Map>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(errors.size());
        errors.forEach(error -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });
        ResponseData responseData = new ResponseData(ResponseCode.NOT_FOUND, map);
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

}
