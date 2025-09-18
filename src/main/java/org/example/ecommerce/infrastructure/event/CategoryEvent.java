package org.example.ecommerce.infrastructure.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.model.category.Category;

@Getter
@RequiredArgsConstructor
public class CategoryEvent {


    private final Category category;
    private final Type eventType;
    private final String performedBy; // اسم المستخدم اللي قام بالعملية

}
