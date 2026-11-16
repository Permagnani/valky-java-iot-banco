package br.com.fiap.valky.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia do RabbitMQ:
 * valky.ficha (exchange) --ficha.atualizada--> valky.ficha.resumo (fila)
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_FICHA = "valky.ficha";
    public static final String FILA_RESUMO = "valky.ficha.resumo";
    public static final String ROUTING_KEY_FICHA_ATUALIZADA = "ficha.atualizada";

    @Bean
    public DirectExchange fichaExchange() {
        return new DirectExchange(EXCHANGE_FICHA, true, false);
    }

    @Bean
    public Queue filaResumo() {
        return QueueBuilder.durable(FILA_RESUMO).build();
    }

    @Bean
    public Binding bindingResumo(Queue filaResumo, DirectExchange fichaExchange) {
        return BindingBuilder.bind(filaResumo)
                .to(fichaExchange)
                .with(ROUTING_KEY_FICHA_ATUALIZADA);
    }
}
