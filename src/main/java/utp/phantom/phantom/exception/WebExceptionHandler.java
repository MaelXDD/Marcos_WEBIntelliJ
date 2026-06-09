package utp.phantom.phantom.exception;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitle", "Petición Inválida");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalWebException(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorTitle", "Error del Sistema");
        mav.addObject("errorMessage", "Ha ocurrido un problema al procesar tu solicitud.");
        mav.setViewName("error/500"); // Luego crearemos el html para el error 500 y 400
        return mav;
    }
}