#!/usr/bin/env zsh

# Counts words in the Design_Report, excluding References and Appendices.
# Usage:
#   scripts/count_report_words.sh docs/report/Design_Report.md
#   scripts/count_report_words.sh Design_Report.pdf   # requires pdftotext

set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <Design_Report.md|Design_Report.pdf>" >&2
  exit 1
fi

file=$1
ext=${file##*.}

if [[ $ext == md ]]; then
  # Extract content up to References heading and count words
  # Assumes headings begin with '## References' and '## Appendices'
  awk 'BEGIN{print_section=1} /^## References/ || /^## Appendices/{print_section=0} {if(print_section) print}' "$file" |\
    sed 's/`\([^`]*\)`/\1/g' |\
    sed 's/\[[^\]]*\]([^)]*)/ /g' |\
    sed 's/\!\[[^\]]*\]([^)]*)/ /g' |\
    sed 's/[#*_>-]//g' |\
    wc -w | awk '{print $1}'
  exit 0
fi

if [[ $ext == pdf ]]; then
  if ! command -v pdftotext >/dev/null 2>&1; then
    echo "pdftotext not found. Install with: brew install poppler" >&2
    exit 2
  fi
  # Convert to text, stop at References/Appendices if headings exist.
  pdftotext -layout "$file" - | awk 'BEGIN{print_section=1} /References/ || /Appendices/{print_section=0} {if(print_section) print}' | wc -w | awk '{print $1}'
  exit 0
fi

echo "Unsupported file type: $ext" >&2
exit 3
