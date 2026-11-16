package br.com.fiap.valky.messaging;

import br.com.fiap.valky.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica o evento "ficha atualizada" no RabbitMQ. A mensagem é só o id da ficha
 * (texto): o consumidor busca os dados atuais no banco, então a ficha, que é
 * sensível, nunca trafega pela fila.
 */
@Component
public class FichaEventoPublisher {

    private static final Logger log = LoggerFactory.getLogger(FichaEventoPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public FichaEventoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * @return true se o evento foi publicado. Se o broker estiver fora do ar o
     *         salvamento da ficha NÃO falha; o resumo pode ser gerado depois
     *         pelo endpoint POST /api/fichas-contexto/usuario/{id}/resumo.
     */
    public boolean publicarFichaAtualizada(Long fichaId) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_FICHA,
                    RabbitConfig.ROUTING_KEY_FICHA_ATUALIZADA,
                    String.valueOf(fichaId));
            log.info("Evento ficha.atualizada publicado (fichaId={})", fichaId);
            return true;
        } catch (AmqpException e) {
            log.warn("Não foi possível publicar ficha.atualizada (fichaId={}): {}",
                    fichaId, e.getMessage());
            return false;
        }
    }
}
