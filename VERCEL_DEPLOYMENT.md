# Vercel Deployment Guide

## Overview
This guide covers deploying the **frontend** to Vercel. The **backend** needs to be deployed separately on a platform that supports Java/Spring Boot.

## Free Backend Hosting Options

### Option 1: Railway (Recommended - $5 free credit/month)
- **Free tier**: $5 credit per month
- **Pros**: Easy setup, automatic PostgreSQL, good documentation
- **Setup**: Already configured in this repo
- **Guide**: See `RAILWAY_DEPLOYMENT.md`

### Option 2: Render (Free tier with limitations)
- **Free tier**: 750 hours/month, spins down after 15 min inactivity
- **Pros**: Truly free, PostgreSQL included
- **Cons**: Cold starts after inactivity
- **URL**: https://render.com

### Option 3: Fly.io (Free tier)
- **Free tier**: 3 shared VMs
- **Pros**: Good performance, global deployment
- **URL**: https://fly.io

## Frontend Deployment to Vercel

### Prerequisites
1. Vercel account (sign up at https://vercel.com)
2. GitHub repository connected
3. Backend deployed and URL available

### Step 1: Deploy Backend First
Deploy your backend to one of the free platforms above and note the backend URL:
- Example: `https://your-backend.railway.app`
- Example: `https://your-backend.onrender.com`

### Step 2: Deploy Frontend to Vercel

#### Method A: Via Vercel Dashboard (Recommended)

1. **Go to Vercel Dashboard**
   - Visit https://vercel.com/dashboard
   - Click "Add New Project"

2. **Import GitHub Repository**
   - Select your repository: `Inventory-Stock-Management-System`
   - Click "Import"

3. **Configure Project**
   - **Framework Preset**: Create React App
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Output Directory**: `build`
   - **Install Command**: `npm install`

4. **Add Environment Variable**
   - Go to "Environment Variables"
   - Add:
     - **Name**: `REACT_APP_API_URL`
     - **Value**: `https://your-backend-url.railway.app/api`
     - **Environment**: Production, Preview, Development (select all)
   - Click "Save"

5. **Deploy**
   - Click "Deploy"
   - Wait for build to complete
   - Your app will be live at: `https://your-app.vercel.app`

#### Method B: Via Vercel CLI

```bash
# Install Vercel CLI
npm i -g vercel

# Navigate to frontend directory
cd frontend

# Login to Vercel
vercel login

# Deploy
vercel

# Set environment variable
vercel env add REACT_APP_API_URL
# Enter: https://your-backend-url.railway.app/api

# Deploy to production
vercel --prod
```

### Step 3: Update CORS on Backend

After deploying frontend, update your backend CORS configuration to allow your Vercel domain:

**In `CorsConfig.java`:**
```java
config.addAllowedOrigin("https://your-app.vercel.app");
// Or use pattern for all Vercel deployments:
config.addAllowedOriginPattern("https://*.vercel.app");
```

Redeploy backend after updating CORS.

## Environment Variables

### Required for Vercel:
- `REACT_APP_API_URL` - Your backend API URL (e.g., `https://backend.railway.app/api`)

### Setting in Vercel Dashboard:
1. Go to Project Settings → Environment Variables
2. Add `REACT_APP_API_URL`
3. Set value to your backend URL
4. Apply to all environments (Production, Preview, Development)

## Troubleshooting

### Build Fails
**Issue**: Build command fails
**Solution**:
- Check that `package.json` has correct build script
- Verify Node.js version (Vercel auto-detects)
- Check build logs in Vercel dashboard

### API Calls Fail (CORS Error)
**Issue**: Frontend can't connect to backend
**Solution**:
1. Verify `REACT_APP_API_URL` is set correctly in Vercel
2. Update backend CORS to allow Vercel domain
3. Check backend is running and accessible
4. Verify backend URL is correct (include `/api` at the end)

### API Calls Return 404
**Issue**: Backend endpoints not found
**Solution**:
- Verify backend URL includes `/api` path
- Check backend is deployed and running
- Test backend URL directly in browser: `https://your-backend.railway.app/api/parts`

### Environment Variable Not Working
**Issue**: `REACT_APP_API_URL` not being used
**Solution**:
- Environment variables must start with `REACT_APP_`
- Redeploy after adding environment variables
- Check variable is set for correct environment (Production/Preview)

## Quick Deployment Checklist

- [ ] Backend deployed and URL available
- [ ] Backend CORS updated for Vercel domain
- [ ] Frontend code pushed to GitHub
- [ ] Vercel project created
- [ ] Root directory set to `frontend`
- [ ] Environment variable `REACT_APP_API_URL` added
- [ ] Build command: `npm install && npm run build`
- [ ] Output directory: `build`
- [ ] Deployed successfully
- [ ] Tested API connectivity

## Post-Deployment

1. **Test Your Application**
   - Visit your Vercel URL
   - Test all features (CRUD operations, file uploads, etc.)
   - Check browser console for errors

2. **Update README**
   - Add live demo link
   - Update deployment instructions

3. **Custom Domain (Optional)**
   - Add custom domain in Vercel settings
   - Update CORS on backend to include custom domain

## Cost Summary

- **Vercel**: Free (unlimited for personal projects)
- **Backend (Railway)**: Free ($5 credit/month)
- **Backend (Render)**: Free (with limitations)
- **Backend (Fly.io)**: Free (3 VMs)
- **Total**: $0/month

## Support

- Vercel Docs: https://vercel.com/docs
- Railway Docs: https://docs.railway.app
- Render Docs: https://render.com/docs

