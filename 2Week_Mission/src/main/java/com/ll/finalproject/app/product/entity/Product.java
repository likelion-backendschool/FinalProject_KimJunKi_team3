package com.ll.finalproject.app.product.entity;

import com.ll.finalproject.app.base.entity.BaseEntity;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Product extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private Member author;

    @ManyToOne(fetch = LAZY)
    private PostKeyword postKeyword;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int price;

    public Product(long id) {
        super(id);
    }

    public void changeModifyForm(String subject, int price, String description) {
        this.subject = subject;
        this.price = price;
        this.description = description;
    }

    public int getSalePrice() {
        return getPrice();
    }

    public int getWholesalePrice() {
        return (int) Math.ceil(getPrice() * 0.7);
    }

    public boolean isOrderable() {
        return true;
    }
    public String getJdenticon() {
        return "product__" + getId();
    }
}
