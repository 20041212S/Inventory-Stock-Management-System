# Fixing 404 Error on Vercel

## Common Causes and Solutions

### 1. Vercel Configuration Issue
The `vercel.json` has been updated to use the correct format for Create React App.

### 2. Build Settings in Vercel Dashboard
Make sure your Vercel project settings are:
- **Framework Preset**: Create React App
- **Root Directory**: `frontend`
- **Build Command**: `npm run build` (or leave empty, Vercel auto-detects)
- **Output Directory**: `build`
- **Install Command**: `npm install` (or leave empty)

### 3. React Router Configuration
If you're still getting 404s on routes:
- The `vercel.json` rewrite rule should handle this
- Make sure `BrowserRouter` is used (not `HashRouter`)

### 4. Environment Variables
Make sure `REACT_APP_API_URL` is set in Vercel:
- Go to Project Settings → Environment Variables
- Add: `REACT_APP_API_URL` = `https://your-backend-url/api`
- Redeploy after adding

### 5. Redeploy After Changes
After updating `vercel.json`:
1. Push changes to GitHub
2. Vercel will auto-deploy
3. Or manually trigger redeploy in Vercel dashboard

## Quick Fix Steps

1. **Update vercel.json** (already done)
2. **Check Vercel Project Settings**:
   - Root Directory: `frontend`
   - Output Directory: `build`
   - Framework: Create React App
3. **Redeploy**:
   - Push to GitHub (auto-deploy)
   - Or click "Redeploy" in Vercel dashboard
4. **Check Build Logs**:
   - Go to Deployments → Latest → View Build Logs
   - Verify build completes successfully
   - Check for any errors

## If Still Getting 404

1. **Check the URL**: Make sure you're accessing the root domain, not a subpath
2. **Check Build Output**: Verify `build/index.html` exists after build
3. **Clear Cache**: Try incognito mode or clear browser cache
4. **Check Console**: Open browser DevTools → Console for errors

## Alternative: Use HashRouter (if rewrites don't work)

If the rewrite still doesn't work, you can use HashRouter instead:

```javascript
// In App.js, change:
import { HashRouter as Router } from 'react-router-dom';
```

This uses `#` in URLs (e.g., `https://app.vercel.app/#/stocks`) but works without server configuration.

