package utp.phantom.phantom.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    /** Archivo mayor al límite de spring.servlet.multipart: se vuelve al formulario con un aviso. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUpload(HttpServletRequest request, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("errorImagen",
                "La imagen supera el tamaño máximo permitido (5 MB).");
        String destino = "/admin/productos";
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                String path = URI.create(referer).getPath();
                // Solo se redirige a pantallas del admin de productos (evita redirecciones abiertas).
                if (path != null && path.startsWith("/admin/productos/")
                        && !path.startsWith("/admin/productos/guardar")) {
                    destino = path;
                }
            } catch (IllegalArgumentException ignored) {
                // Referer malformado: se usa el destino por defecto.
            }
        }
        return "redirect:" + destino;
    }

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