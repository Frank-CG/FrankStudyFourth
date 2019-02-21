package com.example.frankstudyfourth.Protocols;

public final class EventListStructure {
    private class ContentEntity {
        public String title;
        public String iconUri;
        public String entityStatus;
        public String entityStatusDesc;
        public String location;
        public String description;
        public String thumbnailUri;
        public String scheduledStart;
        public String scheduledEnd;
        public String hasArchiveStream;
        public String actualStart;
        public String actualEnd;
        public String lastModifiedTime;
        public String committeeId;
        public String venueId;
        public String assemblyProgress;
        public String assemblyStatus;
        public String foreignKey;
        public String id;
        public String tag;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIconUri() {
            return iconUri;
        }

        public void setIconUri(String iconUri) {
            this.iconUri = iconUri;
        }

        public String getEntityStatus() {
            return entityStatus;
        }

        public void setEntityStatus(String entityStatus) {
            this.entityStatus = entityStatus;
        }

        public String getEntityStatusDesc() {
            return entityStatusDesc;
        }

        public void setEntityStatusDesc(String entityStatusDesc) {
            this.entityStatusDesc = entityStatusDesc;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getThumbnailUri() {
            return thumbnailUri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }

        public String getScheduledStart() {
            return scheduledStart;
        }

        public void setScheduledStart(String scheduledStart) {
            this.scheduledStart = scheduledStart;
        }

        public String getScheduledEnd() {
            return scheduledEnd;
        }

        public void setScheduledEnd(String scheduledEnd) {
            this.scheduledEnd = scheduledEnd;
        }

        public String getHasArchiveStream() {
            return hasArchiveStream;
        }

        public void setHasArchiveStream(String hasArchiveStream) {
            this.hasArchiveStream = hasArchiveStream;
        }

        public String getActualStart() {
            return actualStart;
        }

        public void setActualStart(String actualStart) {
            this.actualStart = actualStart;
        }

        public String getActualEnd() {
            return actualEnd;
        }

        public void setActualEnd(String actualEnd) {
            this.actualEnd = actualEnd;
        }

        public String getLastModifiedTime() {
            return lastModifiedTime;
        }

        public void setLastModifiedTime(String lastModifiedTime) {
            this.lastModifiedTime = lastModifiedTime;
        }

        public String getCommitteeId() {
            return committeeId;
        }

        public void setCommitteeId(String committeeId) {
            this.committeeId = committeeId;
        }

        public String getVenueId() {
            return venueId;
        }

        public void setVenueId(String venueId) {
            this.venueId = venueId;
        }

        public String getAssemblyProgress() {
            return assemblyProgress;
        }

        public void setAssemblyProgress(String assemblyProgress) {
            this.assemblyProgress = assemblyProgress;
        }

        public String getAssemblyStatus() {
            return assemblyStatus;
        }

        public void setAssemblyStatus(String assemblyStatus) {
            this.assemblyStatus = assemblyStatus;
        }

        public String getForeignKey() {
            return foreignKey;
        }

        public void setForeignKey(String foreignKey) {
            this.foreignKey = foreignKey;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    private String lastModifiedTime;
    private ContentEntity contentEntityDatas;

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public ContentEntity getContentEntityDatas() {
        return contentEntityDatas;
    }

    public void setContentEntityDatas(ContentEntity contentEntityDatas) {
        this.contentEntityDatas = contentEntityDatas;
    }
}


