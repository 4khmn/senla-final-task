package com.project.velo.dto.infrastracture;

import org.springframework.core.io.Resource;

public record MediaResource(Resource resource, String contentType) {}
