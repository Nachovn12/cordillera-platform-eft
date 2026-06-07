package cl.duoc.cordillera.bffgateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Configuración de recursos estáticos con fallback SPA.
 *
 * Lógica:
 *   1. Si el recurso existe en classpath:/static/ (JS, CSS, imágenes, etc.) → se sirve tal cual.
 *   2. Si NO existe (rutas SPA como /dashboard, /kpis) → se devuelve index.html
 *      y React Router toma control en el cliente.
 *
 * Esto evita el problema del SpaController anterior, donde el patrón
 * /{path:[^\\.]*}/** interceptaba /assets/index-abc.js antes de que
 * Spring Boot pudiera servirlo con el MIME type correcto.
 */
@Configuration
public class SpaWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location)
                            throws IOException {
                        Resource resource = super.getResource(resourcePath, location);
                        // Recurso encontrado (JS, CSS, imagen…) → servir directamente
                        if (resource != null && resource.exists()) {
                            return resource;
                        }
                        // Ruta SPA sin archivo correspondiente → index.html
                        // React Router resuelve la navegación en el cliente
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
