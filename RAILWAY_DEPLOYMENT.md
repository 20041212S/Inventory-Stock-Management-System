# Railway Deployment Guide

## Prerequisites
1. Railway account (sign up at https://railway.app)
2. GitHub repository connected
3. PostgreSQL database service

## Step-by-Step Deployment

### 1. Create New Project on Railway
1. Go to Railway dashboard
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your repository: `Inventory-Stock-Management-System`
5. Set **Root Directory** to: `backend`

### 2. Add PostgreSQL Database
1. In your Railway project, click "New"
2. Select "Database" → "Add PostgreSQL"
3. Railway will automatically create a PostgreSQL instance
4. Note the connection details (you'll need these)

### 3. Configure Environment Variables
In your backend service, add these environment variables:

**Required:**
- `DATABASE_URL` - Automatically set by Railway when you link PostgreSQL service
- `PORT` - Automatically set by Railway (usually 8080)

**Optional (if DATABASE_URL is not auto-set):**
- `DB_USER` - PostgreSQL username
- `DB_PASSWORD` - PostgreSQL password
- `SPRING_PROFILES_ACTIVE` - Set to `prod` (optional)

### 4. Link PostgreSQL to Backend Service
1. In your backend service settings
2. Go to "Variables" tab
3. Click "Reference Variable"
4. Select your PostgreSQL service
5. Select `DATABASE_URL`
6. Railway will automatically inject the connection string

### 5. Deploy
1. Railway will automatically detect Java/Maven
2. It will build using the `nixpacks.toml` or `railway.json` configuration
3. The build process will:
   - Install Java 17 and Maven
   - Run `mvn clean package -DskipTests`
   - Start the application with `java -jar target/*.jar`

### 6. Get Your Backend URL
After deployment, Railway will provide a URL like:
`https://your-service-name.up.railway.app`

## Troubleshooting

### Build Fails
**Issue:** Maven build fails
**Solution:**
- Check Railway logs for specific error
- Ensure `pom.xml` is valid (no `<n>` tags, should be `<name>`)
- Verify Java version is 17

**Issue:** JAR file not found
**Solution:**
- Check that `spring-boot-maven-plugin` is configured correctly
- Verify build completes successfully
- Check `target/` directory contains the JAR

### Database Connection Fails
**Issue:** Cannot connect to database
**Solution:**
- Verify `DATABASE_URL` environment variable is set
- Check PostgreSQL service is running
- Ensure database service is linked to backend service
- Verify connection string format: `postgresql://user:password@host:port/dbname`

### Port Issues
**Issue:** Application doesn't start
**Solution:**
- Railway provides `PORT` environment variable automatically
- Application should use `${PORT:8080}` in `application.properties`
- Don't hardcode port numbers

### CORS Errors
**Issue:** Frontend can't connect to backend
**Solution:**
- Update `CorsConfig.java` to allow your Railway domain
- Add `config.addAllowedOriginPattern("https://*.up.railway.app");`
- Redeploy backend

## Environment Variables Summary

| Variable | Source | Required |
|----------|--------|----------|
| `DATABASE_URL` | PostgreSQL service (auto) | Yes |
| `PORT` | Railway (auto) | Yes |
| `DB_USER` | Manual (if needed) | No |
| `DB_PASSWORD` | Manual (if needed) | No |

## Testing Deployment

1. Check service logs in Railway dashboard
2. Test health endpoint: `https://your-service.up.railway.app/api/parts`
3. Verify database connection in logs
4. Test API endpoints

## Next Steps

After backend is deployed:
1. Note your backend URL
2. Deploy frontend (separate service or Vercel)
3. Update frontend `REACT_APP_API_URL` to point to Railway backend
4. Update CORS configuration if needed

