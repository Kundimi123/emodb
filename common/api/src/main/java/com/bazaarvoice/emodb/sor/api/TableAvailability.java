package com.bazaarvoice.emodb.sor.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TableAvailability {
    private final String _placement;
    private final boolean _facade;

    public TableAvailability(@JsonProperty ("placement") String placement, @JsonProperty ("facade") boolean facade) {
        _placement = checkNotNull(placement, "Table option is required: placement");
        _facade = facade;
    }

    /**
     * Returns a placement string in the format "keyspace:column_family_prefix".
     */
    public String getPlacement() {
        return _placement;
    }

    /**
     * Returns if this is a facade
     */
    public boolean isFacade() {
        return _facade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TableAvailability)) {
            return false;
        }
        TableAvailability that = (TableAvailability) o;
        return Objects.equal(_placement, that._placement) &&
                Objects.equal(_facade, that._facade);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_placement, _facade);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("placement", _placement)
                .add("facade", _facade)
                .toString();
    }
}
