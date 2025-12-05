# GitHub Workflows Documentation

This document explains all automated workflows configured for the AlgeNord project repositories.

## Table of Contents
- [Overview](#overview)
- [Setup Requirements](#setup-requirements)
- [Workflows](#workflows)
  - [Auto-assign Metadata](#auto-assign-metadata)
  - [Link PR to Issue](#link-pr-to-issue)
  - [Move Closed PR to Done](#move-closed-pr-to-done)
- [Issue Templates](#issue-templates)
- [Pull Request Template](#pull-request-template)
- [Troubleshooting](#troubleshooting)

## Overview

The AlgeNord project uses GitHub Actions to automate project management tasks, ensuring consistency and reducing manual work. These workflows automatically:
- Add issues and PRs to the project board
- Set project fields (Type, Status)
- Apply labels based on content
- Link PRs to issues
- Move completed PRs to "Done"
- Post comments tracking all automated actions

## Setup Requirements

### 1. Personal Access Token (PAT)

Create a Personal Access Token with the following permissions:
- `write:org` - Read and write org projects
- `repo` - Full control of repositories (for private repos)

**Steps:**
1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token (classic)"
3. Name: "Project Automation"
4. Select scopes: `write:org`, `repo`
5. Generate and copy the token

### 2. Add Token as Organization Secret

1. Go to Organization Settings → Secrets and variables → Actions
2. Click "New organization secret"
3. Name: `PROJECT_TOKEN`
4. Value: Paste your PAT
5. Repository access: Select "All repositories" or specific repos
6. Save

### 3. Repository Configuration

In each repository (backend & frontend):
1. Extract `.github/` folder to repository root
2. Ensure branch protection rules are enabled for `main` and `develop`
3. Configure GitHub Rulesets for branch naming enforcement

## Workflows

### Auto-assign Metadata

**File:** `.github/workflows/auto-assign.yml`

**Triggers:** When issues or PRs are opened or labeled

**What it does:**
1. Adds the issue/PR to the Scrum Board (Project #3)
2. Sets the **Type** field based on title:
   - `[BUG]` → Type: Bug
   - `[FEATURE]` or `[STORY]` → Type: Feature
   - `[TASK]` → Type: Task
   - Default → Type: Task
3. Sets the **Status** field:
   - Issues → Status: Backlog
   - PRs → No status set (manual control)
4. Auto-applies labels based on content:
   - `[STORY]` in title → `user story` label
   - `[TECH]` or `[TECHNICAL]` in title → `technical story` label
   - "backend" or "api" in body → `backend` label
   - "database", "db", or "sql" in body → `database` label
   - "documentation", "readme", or "diagram" in body → `documentation` label
5. Posts comment listing all automated actions

**Example Comment:**
```
🤖 Automated Actions

- Type set to: `Feature`
- Status set to: `Backlog`

✅ Added to Scrum Board

🏷️ Auto-applied labels: `user story`, `backend`
```

**Required Permissions:**
- `issues: write`
- `pull-requests: write`
- `contents: read`

**Token Required:** `PROJECT_TOKEN`

---

### Link PR to Issue

**File:** `.github/workflows/link-pr-to-issue.yml`

**Triggers:** When PRs are opened or edited

**What it does:**
1. Extracts issue number from:
   - PR body (looks for "Closes #X", "Fixes #X", "Resolves #X")
   - Branch name (e.g., `feature/#5-description`)
2. Verifies the issue exists
3. Posts comment on PR linking to the issue (if not already in description)
4. Posts comment on the issue that a PR was opened

**Example Comments:**

On PR:
```
🔗 This PR is linked to issue #5
```

On Issue:
```
🔄 Pull request #12 opened for this issue
```

**Required Permissions:**
- `pull-requests: write`
- `issues: write`

**Token Required:** `GITHUB_TOKEN` (default)

---

### Move Closed PR to Done

**File:** `.github/workflows/pr-to-done.yml`

**Triggers:** When PRs are closed (merged or not merged)

**What it does:**
1. Finds the PR in the project board
2. Updates Status field to "Done"
3. Posts comment indicating whether PR was merged or closed without merging

**Example Comment:**

If merged:
```
🎉 This PR was ✅ merged and automatically moved to Done on the Scrum Board
```

If closed without merge:
```
🎉 This PR was 🚫 closed without merging and automatically moved to Done on the Scrum Board
```

**Required Permissions:**
- `pull-requests: read`

**Token Required:** `PROJECT_TOKEN`

---

## Issue Templates

Located in `.github/ISSUE_TEMPLATE/`

### Bug Report (`bug_report.yml`)
- Title prefix: `[BUG]`
- Required fields: Description, Steps to reproduce, Expected behavior, Actual behavior
- Optional: Environment details, Additional context

### User Story (`user_story.yml`)
- Title prefix: `[STORY]`
- Auto-label: `user story`
- Required fields: User story, Description, Acceptance criteria, Priority
- Optional: Mockups/wireframes, Story points

### Task (`task.yml`)
- Title prefix: `[TASK]`
- Required fields: Task description, Category
- Optional: Subtasks, Technical notes, Definition of done

### Configuration (`config.yml`)
- Disables blank issues
- Links to project board

---

## Pull Request Template

**File:** `.github/pull_request_template.md`

**Required sections:**
- Description of changes
- Related issue (must use "Closes #X")
- Type of change (checkbox)
- Changes made (list)
- Testing performed (checkbox)
- Screenshots/videos (if applicable)
- Checklist (checkbox)

**Enforcement:**
Branch protection rules require:
- PR approval from at least 1 reviewer
- All conversations resolved before merge

---

## Troubleshooting

### Workflow fails with "Could not resolve to a ProjectV2"

**Problem:** Project URL is incorrect or token lacks permissions

**Solution:**
1. Verify project URL: `https://github.com/orgs/TeamOrnOps/projects/3`
2. Ensure `PROJECT_TOKEN` secret exists and has `write:org` scope
3. Check token is authorized for the organization

### Labels not being applied

**Problem:** Label names don't match repository labels

**Solution:**
1. Check existing labels in repository settings
2. Verify label names in workflow match exactly (case-sensitive)
3. Current labels: `backend`, `database`, `documentation`, `user story`, `technical story`

### Comments not appearing

**Problem:** Token lacks comment permissions

**Solution:**
1. Verify token has `repo` scope (includes issues/PR comments)
2. Check workflow permissions in repository settings

### PR not linking to issue

**Problem:** Issue number not found in branch name or PR body

**Solution:**
1. Ensure branch follows naming: `feature/#5-description`
2. OR include in PR body: "Closes #5" or "Fixes #5" or "Resolves #5"

### Type/Status not being set

**Problem:** Field names in project don't match workflow

**Solution:**
1. Verify project has fields named exactly "Type" and "Status"
2. Verify Type options: "Bug", "Feature", "Task"
3. Verify Status options: "Backlog", "Done"

---

## Maintenance

### Adding New Labels

1. Add label in repository settings
2. Update `.github/workflows/auto-assign.yml`:
   ```javascript
   if (body.includes('your-keyword')) {
     labels.push('your-label');
   }
   ```

### Changing Project Fields

Update these sections in `auto-assign.yml`:
- Line ~17: Type detection logic
- Line ~24: Status assignment logic
- Line ~67: Field name queries

### Updating Project URL

Update in these files:
1. `.github/workflows/auto-assign.yml` - Line 18
2. `.github/workflows/pr-to-done.yml` - Line 33
3. `.github/ISSUE_TEMPLATE/config.yml` - Line 3

---

## Workflow Status

Check workflow runs:
- Go to repository → Actions tab
- View individual workflow runs and logs
- All console.log statements appear in workflow logs

## Support

For issues with workflows:
1. Check Actions tab for error logs
2. Verify all secrets are configured
3. Ensure branch protection rules are active
4. Check repository permissions for GitHub Actions
