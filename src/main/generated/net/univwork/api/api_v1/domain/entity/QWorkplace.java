package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWorkplace is a Querydsl query type for Workplace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkplace extends EntityPathBase<Workplace> {

    private static final long serialVersionUID = -1555375583L;

    public static final QWorkplace workplace = new QWorkplace("workplace");

    public final NumberPath<Long> commentNum = createNumber("commentNum", Long.class);

    public final StringPath jobDetail = createString("jobDetail");

    public final StringPath note = createString("note");

    public final StringPath preferredDepartment = createString("preferredDepartment");

    public final StringPath preferredGrade = createString("preferredGrade");

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final StringPath requiredNum = createString("requiredNum");

    public final NumberPath<Long> univCode = createNumber("univCode", Long.class);

    public final StringPath univName = createString("univName");

    public final NumberPath<Long> views = createNumber("views", Long.class);

    public final StringPath workDay = createString("workDay");

    public final StringPath workplaceAddress = createString("workplaceAddress");

    public final NumberPath<Long> workplaceCode = createNumber("workplaceCode", Long.class);

    public final StringPath workplaceName = createString("workplaceName");

    public final StringPath workplaceType = createString("workplaceType");

    public final StringPath workTime = createString("workTime");

    public final StringPath workType = createString("workType");

    public QWorkplace(String variable) {
        super(Workplace.class, forVariable(variable));
    }

    public QWorkplace(Path<? extends Workplace> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorkplace(PathMetadata metadata) {
        super(Workplace.class, metadata);
    }

}

