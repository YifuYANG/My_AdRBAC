package app.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomErrorExceptionAdvice {

    @ExceptionHandler(value = CustomErrorException.class)
    public ModelAndView exceptionHandler(CustomErrorException e){
        ModelAndView mv = new ModelAndView();
        mv.addObject("msg", e.getMessage());
        mv.setViewName("error");
        return mv;
    }

}
