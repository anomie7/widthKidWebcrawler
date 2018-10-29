package com.crawling.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInterParkData is a Querydsl query type for InterParkData
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInterParkData extends EntityPathBase<InterParkData> {

    private static final long serialVersionUID = 172543468L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInterParkData interParkData = new QInterParkData("interParkData");

    public final QAddress address;

    public final EnumPath<DeleteFlag> deleteflag = createEnum("deleteflag", DeleteFlag.class);

    public final EnumPath<InterparkType> dtype = createEnum("dtype", InterparkType.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageFilePath = createString("imageFilePath");

    public final StringPath interparkCode = createString("interparkCode");

    public final StringPath location = createString("location");

    public final StringPath name = createString("name");

    public final ListPath<Price, QPrice> price = this.<Price, QPrice>createList("price", Price.class, QPrice.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public QInterParkData(String variable) {
        this(InterParkData.class, forVariable(variable), INITS);
    }

    public QInterParkData(Path<? extends InterParkData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInterParkData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInterParkData(PathMetadata metadata, PathInits inits) {
        this(InterParkData.class, metadata, inits);
    }

    public QInterParkData(Class<? extends InterParkData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new QAddress(forProperty("address")) : null;
    }

}

