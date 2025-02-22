/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.exec.store.easy.sequencefile;

import java.util.List;

import com.dremio.common.logical.FormatPluginConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;

@JsonTypeName("sequencefile") @JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SequenceFileFormatConfig implements FormatPluginConfig {

  public List<String> extensions = ImmutableList.of();

  @Override
  public int hashCode() {
    return (extensions == null)? 0 : extensions.hashCode();
  }

  public List<String> getExtensions() {
    return extensions;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (getClass() == obj.getClass()) {
      SequenceFileFormatConfig other = (SequenceFileFormatConfig) obj;
      return (extensions == null)? (other.extensions == null) : extensions.equals(other.extensions);
    }
    return false;
  }
}
