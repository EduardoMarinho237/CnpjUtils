package com.eduardomarinho.cnpjutils.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CNPJ Utils API")
                        .version("1.0.0")
                        .description("API para validação e geração de CNPJs nos padrões novo e antigo, a partir de julho de 2026, o formato do CNPJ sofrerá uma mudança, que não afeta os CNPJs já existentes mas altera o padrão dos novos CNPJs. Essa API foi criada para facilitar a geração e validação tanto desses novos CNPJs quanto dos antigos, o código está disponível no GitHub: https://github.com/EduardoMarinho237/CnpjUtils para que qualquer um possa ler e contribuir com melhorias.")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}