package mai.student.tokenizers.python3.ast.nodes.ctx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = Load.class, name = "Load"),
        @JsonSubTypes.Type(value = Store.class, name = "Store"),
        @JsonSubTypes.Type(value = Del.class, name = "Del")
})
@Getter
public abstract class Ctx {

    @JsonProperty("_type")
    private String type;

    enum Type {
        LOAD, STORE, DEL
    }
}
