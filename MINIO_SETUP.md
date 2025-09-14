# MinIO Avatar Setup Guide

## 🚀 Quick Setup

### 1. Start Services
```bash
cd docker
docker-compose up -d
```

### 2. Configure MinIO CORS (Windows)
```bash
cd docker
setup-minio.bat
```

### 3. Configure MinIO CORS (Linux/Mac)
```bash
cd docker
chmod +x minio-cors-setup.sh
./minio-cors-setup.sh
```

## 🔧 Manual Setup

### 1. Access MinIO Console
- URL: http://localhost:9001
- Username: `minioadmin`
- Password: `minioadmin123`

### 2. Create Buckets
- `quizizz-avatars` (for user avatars)
- `quizizz-images` (for quiz images)

### 3. Set Bucket Policy
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {"AWS": "*"},
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::quizizz-avatars/*"
    }
  ]
}
```

### 4. Configure CORS
```json
{
  "CORSRules": [
    {
      "AllowedOrigins": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
      "AllowedHeaders": ["*"],
      "ExposeHeaders": ["ETag"],
      "MaxAgeSeconds": 3600
    }
  ]
}
```

## 🧪 Test Avatar Upload

### 1. Test API Endpoint
```bash
curl -X POST http://localhost:8080/api/v1/profile/avatar \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@avatar.jpg"
```

### 2. Test Avatar URL
```bash
curl -X GET http://localhost:8080/api/v1/profile/avatar \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Test Direct MinIO Access
```bash
curl -I "PRESIGNED_URL_FROM_API"
```

## 🐛 Troubleshooting

### CORS Error
- Check MinIO CORS configuration
- Verify bucket policy is public
- Ensure Spring Boot CORS allows frontend origin

### 404 Not Found
- Verify bucket exists: `quizizz-avatars`
- Check file was uploaded successfully
- Validate presigned URL format

### Network Error
- Check MinIO is running on port 9000
- Verify Docker network connectivity
- Test MinIO console access

## 📝 Frontend Integration

```javascript
// React component example
const Avatar = ({ userId }) => {
  const [avatarUrl, setAvatarUrl] = useState(null);
  
  useEffect(() => {
    fetch('/api/v1/profile/avatar', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(data => setAvatarUrl(data.data))
    .catch(() => setAvatarUrl(null));
  }, [userId]);
  
  return (
    <img 
      src={avatarUrl || '/default-avatar.png'} 
      alt="Avatar"
      onError={(e) => e.target.src = '/default-avatar.png'}
    />
  );
};
```