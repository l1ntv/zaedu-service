package src.main.java.ru.tbank.zaedu.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import src.main.java.ru.tbank.zaedu.models.AbstractEntity;

@RequiredArgsConstructor
public class EntityController<E extends AbstractEntity> {

    protected final ModelMapper modelMapper;

    protected <T> T serialize(E entity, Class<T> toClass) {
        return modelMapper.map(entity, toClass);
    }

    protected <T> List<T> serialize(List<E> entities, Class<T> toClass) {
        return entities.stream().map(it -> serialize(it, toClass)).toList();
    }
}
