package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUniversity is a Querydsl query type for University
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUniversity extends EntityPathBase<University> {

    private static final long serialVersionUID = -708143517L;

    public static final QUniversity university = new QUniversity("university");

    public final StringPath academicSystem = createString("academicSystem");

    public final StringPath establishment = createString("establishment");

    public final StringPath region = createString("region");

    public final NumberPath<Long> univCode = createNumber("univCode", Long.class);

    public final StringPath univName = createString("univName");

    public final StringPath univUuid = createString("univUuid");

    public final NumberPath<Integer> workplaceNum = createNumber("workplaceNum", Integer.class);

    public QUniversity(String variable) {
        super(University.class, forVariable(variable));
    }

    public QUniversity(Path<? extends University> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUniversity(PathMetadata metadata) {
        super(University.class, metadata);
    }

}

