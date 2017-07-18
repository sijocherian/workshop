package dev.cjo.starter.ms.syndtarget.service.impl;

/**
 * @author sijocherian
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

//Sample for system wide handler
@ControllerAdvice
public class ServiceExceptionHandler {

    public ServiceExceptionHandler() {
    }


    //Note: let spring handle the exception, exposes exception name with package
    /*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
     @ExceptionHandler(Exception.class)
    //Default reponse have no field. how to get custom fields (ErrorInfo.class)
    public ErrorInfo handleGeneralError(Exception ex) {
            LOGGER.error("Processing error: ", ex);
        return new ErrorInfo(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }*/



    //Spring has long provided a simple but convenient implementation of HandlerExceptionResolver that you may well find being used in your appication already - the SimpleMappingExceptionResolver.

    /*@ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "redirect:/uploadStatus";

    }*/

    private final  static Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandler.class);

}
