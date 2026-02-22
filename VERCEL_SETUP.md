# Vercel Setup Instructions - Fix 404 Error

## Critical Settings in Vercel Dashboard

The 404 error is likely due to incorrect project settings. Follow these steps:

### Step 1: Delete and Recreate Project (Recommended)

1. Go to Vercel Dashboard
2. Delete the current project
3. Create a new project from GitHub
4. **IMPORTANT SETTINGS:**

### Step 2: Configure Project Settings

When importing/creating the project, set these **exact** values:

```
Framework Preset: Create React App
Root Directory: frontend
Build Command: npm run build
Output Directory: build
Install Command: npm install
```

**OR** leave Build Command and Install Command empty (Vercel will auto-detect)

### Step 3: Verify Settings After Creation

1. Go to **Settings** → **General**
2. Verify:
   - **Root Directory**: `frontend` (not empty, not `/`)
   - **Framework**: Create React App
   - **Build Command**: `npm run build` or empty
   - **Output Directory**: `build`
   - **Install Command**: `npm install` or empty

### Step 4: Environment Variables

1. Go to **Settings** → **Environment Variables**
2. Add:
   - **Name**: `REACT_APP_API_URL`
   - **Value**: `https://your-backend-url.railway.app/api`
   - **Environments**: Production, Preview, Development (select all)

### Step 5: Redeploy

1. Go to **Deployments**
2. Click **"Redeploy"** on the latest deployment
3. Or push a new commit to trigger auto-deploy

## Alternative: Manual Configuration via vercel.json

If the dashboard settings don't work, the `vercel.json` should be minimal:

```json
{
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

## Troubleshooting 404 Error

### Check 1: Build Logs
1. Go to **Deployments** → Latest deployment
2. Click **"View Build Logs"**
3. Verify:
   - Build completes successfully
   - No errors in the logs
   - Output shows "Build completed"

### Check 2: Root Directory
- **Wrong**: Root directory is `/` or empty
- **Correct**: Root directory is `frontend`

### Check 3: Output Directory
- **Wrong**: Output directory is `/` or `dist`
- **Correct**: Output directory is `build`

### Check 4: Framework Detection
- Vercel should detect "Create React App"
- If not, manually set it in settings

### Check 5: File Structure
Verify your GitHub repo has this structure:
```
Inventory-Stock-Management-System/
  ├── frontend/
  │   ├── package.json
  │   ├── src/
  │   ├── public/
  │   └── vercel.json
  └── backend/
```

## Quick Fix: Use HashRouter (Temporary)

If rewrites still don't work, temporarily use HashRouter:

1. Edit `frontend/src/App.js`:
```javascript
// Change this line:
import { BrowserRouter as Router } from 'react-router-dom';

// To:
import { HashRouter as Router } from 'react-router-dom';
```

2. Commit and push
3. URLs will use `#` (e.g., `app.vercel.app/#/stocks`)
4. This works without server configuration

## Verify Deployment

After fixing settings:

1. **Check Build**: Deployment should show "Ready"
2. **Visit URL**: Go to `https://your-app.vercel.app`
3. **Check Console**: Open browser DevTools → Console
   - Should see no 404 errors
   - React app should load

## Common Mistakes

❌ **Wrong**: Root Directory = `/` (root of repo)
✅ **Correct**: Root Directory = `frontend`

❌ **Wrong**: Output Directory = `/` or `dist`
✅ **Correct**: Output Directory = `build`

❌ **Wrong**: Framework = Other or Auto
✅ **Correct**: Framework = Create React App

❌ **Wrong**: Missing `vercel.json` rewrite rules
✅ **Correct**: Has rewrite rule for SPA routing

## Still Not Working?

1. **Check Vercel Logs**: Look for specific error messages
2. **Try HashRouter**: As temporary workaround
3. **Contact Support**: Vercel support is very responsive
4. **Check GitHub**: Ensure all files are pushed correctly

