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
package dev.cjo.starter.ms.persist.elasticsearch;

//import lombok.*;

import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 */
/*@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder*/
public class Car {
	//@Id doesn't work without Document annotation


	private String name;
	private String model;

	@LastModifiedDate
	private ZonedDateTime lastModDate;

	//@Field(type = FieldType.Date)
	//@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Long releaseTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public ZonedDateTime getLastModDate() {
		return lastModDate;
	}

	public void setLastModDate(ZonedDateTime lastModDate) {
		this.lastModDate = lastModDate;
	}

	public Long getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Long releaseTime) {
		this.releaseTime = releaseTime;
	}
}
