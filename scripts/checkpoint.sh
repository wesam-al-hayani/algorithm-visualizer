#!/usr/bin/env sh
set -u

if [ "$#" -lt 1 ]; then
  echo "Usage: scripts/checkpoint.sh \"commit message\""
  exit 2
fi

git add -A
if git diff --cached --quiet; then
  echo "Nothing to commit."
else
  git commit -m "$1" || exit 1
fi

branch=$(git branch --show-current)
if git remote get-url origin >/dev/null 2>&1; then
  if git push -u origin "$branch"; then
    echo "Checkpoint pushed to origin/$branch."
  else
    echo "Push failed; the local commit is safe and remains the source of truth." >&2
    exit 0
  fi
else
  echo "No origin configured; the local commit is safe." >&2
fi

