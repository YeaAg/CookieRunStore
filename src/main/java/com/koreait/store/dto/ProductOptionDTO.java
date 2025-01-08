package com.koreait.store.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductOptionDTO  implements Comparable<ProductOptionDTO> {
    @EqualsAndHashCode.Include
    private Integer no;
    private String name;
    private Integer price;
    private Integer amount;

    @Override
    public int compareTo(ProductOptionDTO o) {
        return no - o.no;
    }
}
