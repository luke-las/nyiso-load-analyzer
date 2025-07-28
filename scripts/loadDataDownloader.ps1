param(
    [Parameter(Mandatory=$true)]
    [datetime]$StartDate,

    [Parameter(Mandatory=$true)]
    [datetime]$EndDate,

    [string]$DownloadFolder = "$PSScriptRoot\downloaded_files"
)

# Create folder if not exists
if (-not (Test-Path $DownloadFolder)) {
    New-Item -ItemType Directory -Path $DownloadFolder | Out-Null
}

# Function to download and extract one ZIP file for a given month
function DownloadAndExtractZip($yearMonth) {
    $zipUrl = "https://mis.nyiso.com/public/csv/pal/${yearMonth}01pal_csv.zip"
    $zipPath = Join-Path $DownloadFolder "$yearMonth.zip"
    $extractFolder = Join-Path $DownloadFolder $yearMonth

    # Download if not exists
    if (-not (Test-Path $zipPath)) {
        Write-Host "Downloading $zipUrl"
        try {
            Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath -ErrorAction Stop
        }
        catch {
            Write-Warning "Failed to download $zipUrl"
            return $false
        }
    }
    else {
        Write-Host "Already downloaded: $zipPath"
    }

    # Extract if folder not exists
    if (-not (Test-Path $extractFolder)) {
        Write-Host "Extracting $zipPath"
        Expand-Archive -Path $zipPath -DestinationPath $extractFolder -Force
    }
    else {
        Write-Host "Already extracted: $extractFolder"
    }
    return $true
}

# Iterate from EndDate back to StartDate, month by month
$date = $EndDate
while ($date -ge $StartDate) {
    $yearMonth = $date.ToString("yyyyMM")
    DownloadAndExtractZip $yearMonth | Out-Null

    # Move to previous month
    $date = $date.AddMonths(-1)
}

Write-Host "Download and extraction complete."