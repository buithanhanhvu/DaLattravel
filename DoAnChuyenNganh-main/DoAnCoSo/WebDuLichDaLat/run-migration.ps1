# Script quản lý Migrations cho WebDuLichDaLat
# Mục đích: Tự động hóa các thao tác Entity Framework Core Migrations

Write-Host "----------------------------------------" -ForegroundColor Cyan
Write-Host "   HE THONG QUAN LY MIGRATIONS         " -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra sự tồn tại của project file
if (-Not (Test-Path "WebDuLichDaLat.csproj")) {
    Write-Host "Loi: Khong tim thay file WebDuLichDaLat.csproj" -ForegroundColor Red
    Write-Host "Goi y: Hay chay script nay trong thu muc goc cua project WebDuLichDaLat." -ForegroundColor Yellow
    exit 1
}

Write-Host "Xac nhan: Da tim thay project file." -ForegroundColor Green
Write-Host ""

# Danh mục thao tác
Write-Host "Chon thao tac can thuc hien:" -ForegroundColor Yellow
Write-Host "1. Khoi tao Migration moi (Add-Migration)" -ForegroundColor White
Write-Host "2. Cap nhat co so du lieu (Update-Database)" -ForegroundColor White
Write-Host "3. Liet ke danh sach Migrations hien tai" -ForegroundColor White
Write-Host "4. Hoan tac Migration cuoi cung (neu chua apply)" -ForegroundColor White
Write-Host "5. Quy trinh: Tao Migration moi va Cap nhat ngay" -ForegroundColor White
Write-Host "6. Thoat" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Nhap lua chon cua ban (1-6)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "Dang chuan bi tao Migration moi..." -ForegroundColor Cyan
        $migrationName = Read-Host "Nhap ten Migration (vi du: InitialCreate)"
        
        if ([string]::IsNullOrWhiteSpace($migrationName)) {
            Write-Host "Loi: Ten Migration khong duoc de trong!" -ForegroundColor Red
            exit 1
        }
        
        Write-Host "Dang thuc thi: dotnet ef migrations add $migrationName" -ForegroundColor Yellow
        dotnet ef migrations add $migrationName --project . --startup-project .
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Thong bao: Migration da duoc tao thanh cong." -ForegroundColor Green
        } else {
            Write-Host "Loi: Co loi xay ra trong qua trinh tao Migration." -ForegroundColor Red
        }
    }
    
    "2" {
        Write-Host ""
        Write-Host "Dang chuan bi cap nhat co so du lieu..." -ForegroundColor Cyan
        Write-Host "Canh bao: Thao tac nay se thay doi cau truc co so du lieu hien tai!" -ForegroundColor Yellow
        $confirm = Read-Host "Ban co chac chan muon tiep tuc? (y/n)"
        
        if ($confirm -eq "y" -or $confirm -eq "Y") {
            dotnet ef database update --project . --startup-project .
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Thong bao: Co so du lieu da duoc cap nhat thanh cong." -ForegroundColor Green
            } else {
                Write-Host "Loi: Co loi khi cap nhat co so du lieu." -ForegroundColor Red
            }
        } else {
            Write-Host "Thong bao: Thao tac da duoc huy bo." -ForegroundColor Yellow
        }
    }
    
    "3" {
        Write-Host ""
        Write-Host "Danh sach cac Migrations hien tai:" -ForegroundColor Cyan
        dotnet ef migrations list --project . --startup-project .
    }
    
    "4" {
        Write-Host ""
        Write-Host "Dang chuan bi xoa Migration cuoi cung..." -ForegroundColor Cyan
        Write-Host "Canh bao: Chi nen thuc hien neu Migration nay chua duoc ap dung vao DB!" -ForegroundColor Yellow
        $confirm = Read-Host "Ban co chac chan muon tiep tuc? (y/n)"
        
        if ($confirm -eq "y" -or $confirm -eq "Y") {
            dotnet ef migrations remove --project . --startup-project .
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Thong bao: Migration cuoi cung da duoc go bo." -ForegroundColor Green
            } else {
                Write-Host "Loi: Khong the xoa Migration." -ForegroundColor Red
            }
        } else {
            Write-Host "Thong bao: Thao tac da duoc huy bo." -ForegroundColor Yellow
        }
    }
    
    "5" {
        Write-Host ""
        Write-Host "Chuan bi quy trinh tao va cap nhat Migration..." -ForegroundColor Cyan
        $migrationName = Read-Host "Nhap ten Migration"
        
        if ([string]::IsNullOrWhiteSpace($migrationName)) {
            Write-Host "Loi: Ten Migration khong duoc de trong!" -ForegroundColor Red
            exit 1
        }
        
        Write-Host "Buoc 1: Dang tao Migration..." -ForegroundColor Yellow
        dotnet ef migrations add $migrationName --project . --startup-project .
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Thong bao: Tao Migration thanh cong." -ForegroundColor Green
            Write-Host ""
            Write-Host "Canh bao: Buoc tiep theo se thay doi du lieu thuc te." -ForegroundColor Yellow
            $confirm = Read-Host "Ban co muon thuc hien cap nhat database ngay bay gio? (y/n)"
            
            if ($confirm -eq "y" -or $confirm -eq "Y") {
                Write-Host "Buoc 2: Dang cap nhat co so du lieu..." -ForegroundColor Yellow
                dotnet ef database update --project . --startup-project .
                
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "Thong bao: Hoan thanh quy trinh cap nhat database." -ForegroundColor Green
                } else {
                    Write-Host "Loi: Loi khi cap nhat database tai buoc 2." -ForegroundColor Red
                }
            }
        } else {
            Write-Host "Loi: That bai ngay tai buoc tao Migration." -ForegroundColor Red
        }
    }
    
    "6" {
        Write-Host "Ket thuc chuong trinh." -ForegroundColor Cyan
        exit 0
    }
    
    default {
        Write-Host "Loi: Lua chon khong hop le!" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "----------------------------------------" -ForegroundColor Cyan
Write-Host " Thao tac hoan tat!" -ForegroundColor Green
Write-Host "----------------------------------------" -ForegroundColor Cyan


