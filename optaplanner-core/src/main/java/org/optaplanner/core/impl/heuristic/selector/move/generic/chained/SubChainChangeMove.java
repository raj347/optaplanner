/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class SubChainChangeMove extends AbstractMove {

    private final SubChain subChain;
    private final GenuineVariableDescriptor variableDescriptor;
    protected final SingletonInverseVariableSupply inverseVariableSupply;
    private final Object toPlanningValue;

    public SubChainChangeMove(SubChain subChain, GenuineVariableDescriptor variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply, Object toPlanningValue) {
        this.subChain = subChain;
        this.variableDescriptor = variableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.toPlanningValue = toPlanningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        if (subChain.getEntityList().contains(toPlanningValue)) {
            return false;
        }
        Object oldFirstValue = variableDescriptor.getValue(subChain.getFirstEntity());
        return !Objects.equals(oldFirstValue, toPlanningValue);
    }

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldFirstValue = variableDescriptor.getValue(subChain.getFirstEntity());
        return new SubChainChangeMove(subChain, variableDescriptor, inverseVariableSupply, oldFirstValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        Object firstEntity = subChain.getFirstEntity();
        Object lastEntity = subChain.getLastEntity();
        Object oldFirstValue = variableDescriptor.getValue(firstEntity);
        Object oldTrailingLastEntity = inverseVariableSupply.getInverseSingleton(lastEntity);
        Object newTrailingEntity = toPlanningValue == null ? null
                : inverseVariableSupply.getInverseSingleton(toPlanningValue);
        // Close the old chain
        if (oldTrailingLastEntity != null) {
            scoreDirector.changeVariableFacade(variableDescriptor, oldTrailingLastEntity, oldFirstValue);
        }
        // Change the entity
        scoreDirector.changeVariableFacade(variableDescriptor, firstEntity, toPlanningValue);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.changeVariableFacade(variableDescriptor, newTrailingEntity, lastEntity);
        }
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return subChain.getEntityList();
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SubChainChangeMove) {
            SubChainChangeMove other = (SubChainChangeMove) o;
            return new EqualsBuilder()
                    .append(subChain, other.subChain)
                    .append(variableDescriptor, other.variableDescriptor)
                    .append(toPlanningValue, other.toPlanningValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(subChain)
                .append(variableDescriptor)
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        Object oldFirstValue = variableDescriptor.getValue(subChain.getFirstEntity());
        return subChain.toDottedString() + " {" + oldFirstValue + " -> " + toPlanningValue + "}";
    }

}
