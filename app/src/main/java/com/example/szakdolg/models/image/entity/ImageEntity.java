package com.example.szakdolg.models.image.entity;

public class ImageEntity {

   private int id;
   private String fileName;
   private Long userId;
   private String imageUri;
   private String mimeType;
   private int width;
   private int height;
   private long size;
   private long dateAdded;
   private String status;
   private String tags;
   private String uuid;

   public ImageEntity(
      String imageUri,
      Long userId,
      String mimeType,
      long dateAdded,
      String status,
      String tags,
      String uuid
   ) {
      this.imageUri = imageUri;
      this.userId = userId;
      this.mimeType = mimeType;
      this.dateAdded = dateAdded;
      this.status = status;
      this.tags = tags;
      this.uuid = uuid;
   }

   public ImageEntity(
      String fileName,
      Long userId,
      String imageUri,
      String mimeType,
      long dateAdded,
      String status,
      String tags,
      String uuid
   ) {
      this.fileName = fileName;
      this.userId = userId;
      this.imageUri = imageUri;
      this.mimeType = mimeType;
      this.dateAdded = dateAdded;
      this.status = status;
      this.tags = tags;
      this.uuid = uuid;
   }

   public ImageEntity() {}

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getFileName() {
      return fileName;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public String getImageUri() {
      return imageUri;
   }

   public void setImageUri(String imageUri) {
      this.imageUri = imageUri;
   }

   public String getMimeType() {
      return mimeType;
   }

   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public long getSize() {
      return size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public long getDateAdded() {
      return dateAdded;
   }

   public void setDateAdded(long dateAdded) {
      this.dateAdded = dateAdded;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }
}
