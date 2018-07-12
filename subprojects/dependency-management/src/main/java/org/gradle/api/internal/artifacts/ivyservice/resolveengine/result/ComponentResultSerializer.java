/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.result;

import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.result.ComponentSelectionReason;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.internal.artifacts.ImmutableModuleIdentifierFactory;
import org.gradle.api.internal.artifacts.ModuleVersionIdentifierSerializer;
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.ComponentResult;
import org.gradle.internal.serialize.Decoder;
import org.gradle.internal.serialize.Encoder;
import org.gradle.internal.serialize.Serializer;

import java.io.IOException;

public class ComponentResultSerializer implements Serializer<ComponentResult> {

    private final ModuleVersionIdentifierSerializer idSerializer;
    private final ComponentSelectionReasonSerializer reasonSerializer;
    private final ComponentIdentifierSerializer componentIdSerializer;
    private final AttributeContainerSerializer attributeContainerSerializer;

    public ComponentResultSerializer(ImmutableModuleIdentifierFactory moduleIdentifierFactory, AttributeContainerSerializer attributeContainerSerializer) {
        idSerializer = new ModuleVersionIdentifierSerializer(moduleIdentifierFactory);
        this.attributeContainerSerializer = attributeContainerSerializer;
        reasonSerializer = new ComponentSelectionReasonSerializer();
        componentIdSerializer = new ComponentIdentifierSerializer();
    }

    public ComponentResult read(Decoder decoder) throws IOException {
        long resultId = decoder.readSmallLong();
        ModuleVersionIdentifier id = idSerializer.read(decoder);
        ComponentSelectionReason reason = reasonSerializer.read(decoder);
        ComponentIdentifier componentId = componentIdSerializer.read(decoder);
        String variantName = decoder.readString();
        AttributeContainer attributes = attributeContainerSerializer.read(decoder);
        String repositoryId = decoder.readNullableString();
        return new DetachedComponentResult(resultId, id, reason, componentId, variantName, attributes, repositoryId);
    }

    public void write(Encoder encoder, ComponentResult value) throws IOException {
        encoder.writeSmallLong(value.getResultId());
        idSerializer.write(encoder, value.getModuleVersion());
        reasonSerializer.write(encoder, value.getSelectionReason());
        componentIdSerializer.write(encoder, value.getComponentId());
        encoder.writeString(value.getVariantName().getDisplayName());
        attributeContainerSerializer.write(encoder, value.getVariantAttributes());
        encoder.writeNullableString(value.getRepositoryId());
    }

    void reset() {
        reasonSerializer.reset();
    }
}
