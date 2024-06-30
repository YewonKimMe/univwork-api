package net.univwork.api.api_v1.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.univwork.api.api_v1.domain.entity.Workplace;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Getter @Setter
@ToString
@EqualsAndHashCode
public class WorkplaceDetailDto {

    private Workplace workplace;

    private PagedModel<EntityModel<CommentDto>> workplaceComment;

    public WorkplaceDetailDto(Workplace workplace, PagedModel<EntityModel<CommentDto>> workplaceComment) {
        this.workplace = workplace;
        this.workplaceComment = workplaceComment;
    }
}
