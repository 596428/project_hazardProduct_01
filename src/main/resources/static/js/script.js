// Common functions (공통 기능)
const commonFunctions = {
    logout() {
        fetch('/api/users/logout', {
            method: 'POST',
        })
        .then(() => {
            window.location.href = '/login';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('로그아웃 처리 중 오류가 발생했습니다.');
        });
    },

    // API 응답 처리를 위한 공통 함수
    handleApiResponse(response, successCallback, errorMessage = '처리 중 오류가 발생했습니다.') {
        response.json()
            .then(data => {
                if (data.success) {
                    successCallback(data);
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '오류',
                        text: data.message || errorMessage
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: errorMessage
                });
            });
    },

    // 날짜 포맷팅 함수
    formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString();
    }
};

// Login page functions
const loginFunctions = {
    login() {
        fetch('/api/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: document.getElementById('email').value,
                password: document.getElementById('password').value
            }),
            credentials: 'include'  // 쿠키 포함
        })
        .then(response => {
            console.log('Login response:', response);
            return response.json();
        })
        .then(data => {
            console.log('Login response data:', data);
            if (data.status === 'SUCCESS') {
                window.location.href = '/test01';
            } else {
                alert(data.message || '로그인에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Login error:', error);
            alert('로그인 처리 중 오류가 발생했습니다.');
        });
    },

    init() {
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.login();
            });
        }
    }
};

// Signup page functions
const signupFunctions = {
    validateEmail() {
        const pattern = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+/;
        const email = document.getElementById('email').value;
        const emailError = document.getElementById('emailError');
        
        if (!pattern.test(email)) {
            emailError.textContent = '유효하지 않은 이메일 형식입니다.';
            return false;
        }
        emailError.textContent = '';
        return true;
    },

    validatePassword() {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const passwordError = document.getElementById('passwordError');

        if (password !== confirmPassword) {
            passwordError.textContent = '비밀번호가 일치하지 않습니다.';
            return false;
        }
        passwordError.textContent = '';
        return true;
    },

    validateForm() {
        const isEmailValid = this.validateEmail();
        const isPasswordValid = this.validatePassword();
        const hasPassword = document.getElementById('password').value.trim() !== '';
        
        document.getElementById('submitBtn').disabled = !(isEmailValid && isPasswordValid && hasPassword);
    },

    signup(event) {
        event.preventDefault();
        
        if (!this.validateEmail() || !this.validatePassword()) {
            return;
        }

        const formData = {
            userName: document.querySelector('input[name="userName"]').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        };

        console.log('Sending signup request:', formData); // 디버깅용

        fetch('/api/users/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            console.log('Server response:', response); // 디버깅용
            
            // 응답 타입 확인
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return response.json().then(data => {
                    if (!response.ok) {
                        throw new Error(data.message || '회원가입 중 오류가 발생했습니다.');
                    }
                    return data;
                });
            } else {
                return response.text().then(text => {
                    console.error('Non-JSON response:', text); // 디버깅용
                    throw new Error('서버 오류가 발생했습니다.');
                });
            }
        })
        .then(data => {
            console.log('Parsed response data:', data); // 디버깅용
            
            if (data.status === 'SUCCESS') {
                Swal.fire({
                    title: '성공!',
                    text: '회원가입이 완료되었습니다.',
                    icon: 'success'
                }).then(() => {
                    window.location.href = '/login';
                });
            } else {
                throw new Error(data.message || '회원가입 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error details:', error); // 디버깅용
            Swal.fire({
                title: '오류',
                text: error.message || '회원가입 중 오류가 발생했습니다.',
                icon: 'error'
            });
        });
    },

    init() {
        const form = document.getElementById('signupForm');
        if (form) {
            form.addEventListener('submit', (e) => this.signup(e));
            
            // 이메일 유효성 검사
            const emailInput = document.getElementById('email');
            if (emailInput) {
                emailInput.addEventListener('input', () => {
                    const isEmailValid = this.validateEmail();
                    const isPasswordValid = this.validatePassword();
                    document.getElementById('submitBtn').disabled = !(isEmailValid && isPasswordValid);
                });
            }

            // 비밀번호 확인 유효성 검사
            const passwordInputs = document.querySelectorAll('input[type="password"]');
            passwordInputs.forEach(input => {
                input.addEventListener('input', () => {
                    const isEmailValid = this.validateEmail();
                    const isPasswordValid = this.validatePassword();
                    document.getElementById('submitBtn').disabled = !(isEmailValid && isPasswordValid);
                });
            });
        }
    }
};

// Test01 page functions
const test01Functions = {
    modifiedCells: new Map(),
    isAdmin: false,  // 이 값은 HTML에서 설정할 예정

    initEditableCells() {
        if (this.isAdmin) {
            document.querySelectorAll('.editable').forEach(cell => {
                cell.addEventListener('dblclick', (e) => this.handleCellEdit(e.target));
            });
        }
    },

    handleCellEdit(cell) {
        if (cell.querySelector('input')) return;
        
        const originalValue = cell.textContent;
        const maxLength = cell.getAttribute('data-max');
        const field = cell.getAttribute('data-field');
        const id = cell.getAttribute('data-id');
        
        const input = document.createElement('input');
        input.value = originalValue;
        input.style.width = '100%';
        
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                input.blur();
            }
        });
        
        input.addEventListener('blur', () => this.handleInputBlur(input, cell, originalValue, maxLength, field, id));
        
        cell.textContent = '';
        cell.appendChild(input);
        input.focus();
    },

    handleInputBlur(input, cell, originalValue, maxLength, field, id) {
        const newValue = input.value.trim();
        
        if (newValue.length > maxLength) {
            alert(`입력 가능한 최대 길이는 ${maxLength}자입니다.`);
            cell.textContent = originalValue;
            return;
        }
        
        cell.textContent = newValue;
        
        if (newValue !== originalValue) {
            cell.style.backgroundColor = 'yellow';
            const cellId = id || '';
            const cellDataId = cell.getAttribute('data-data-id') || '';
            
            this.modifiedCells.set(cellId + '_' + field, {
                id: cellId,
                dataId: cellDataId,
                field: field,
                value: newValue
            });
        }
    },

    saveChanges() {
        if (this.modifiedCells.size === 0) {
            alert('수정된 내용이 없습니다.');
            return;
        }
        
        if (!confirm('DB에 반영하시겠습니까?')) return;
        
        const changes = Array.from(this.modifiedCells.values());
        
        fetch('/api/dangerous-goods/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(changes)
        })
        .then(response => {
            console.log('Update response:', response);
            return response.json();
        })
        .then(data => {
            console.log('Update response data:', data);
            
            if (data.status === 'SUCCESS') {
                Swal.fire({
                    icon: 'success',
                    title: '성공',
                    text: data.data || '수정이 완료되었습니다.'
                }).then(() => {
                    location.reload();
                });
            } else {
                throw new Error(data.message || data.data || '수정 실패');
            }
        })
        .catch(error => {
            console.error('Update error:', error);
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '수정 중 오류가 발생했습니다.'
            });
        });
    },

    searchProducts() {
        const keyword = document.getElementById('searchInput').value;
        window.location.href = '/test01?keyword=' + encodeURIComponent(keyword);
    },

    init() {
        // 검색 입력창 엔터 이벤트
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.searchProducts();
                }
            });
        }

        // 수정 가능한 셀 초기화
        this.initEditableCells();
    }
};

// Test03 (MyPage) functions
const myPageFunctions = {
    createNewKey: function() {
        Swal.fire({
            title: 'API 키 생성',
            input: 'text',
            inputLabel: 'API 키 이름을 입력하세요',
            inputPlaceholder: '예: My API Key',
            showCancelButton: true,
            confirmButtonText: '생성',
            cancelButtonText: '취소',
            inputValidator: (value) => {
                if (!value) {
                    return 'API 키 이름을 입력해주세요!';
                }
            }
        }).then((result) => {
            if (result.isConfirmed) {
                fetch('/api/keys/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        keyName: result.value
                    })
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('API 키 생성에 실패했습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        Swal.fire('성공', 'API 키가 생성되었습니다.', 'success');
                        this.loadApiKeys();  // 목록 새로고침
                    } else {
                        throw new Error(data.message || 'API 키 생성에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire('오류', error.message, 'error');
                });
            }
        });
    },

    loadApiKeys: function() {
        fetch('/api/keys/list')
            .then(response => {
                if (!response.ok) {
                    throw new Error('API 키 목록을 불러오는데 실패했습니다.');
                }
                return response.json();
            })
            .then(data => {
                const tbody = document.querySelector('#apiKeysTable tbody');
                tbody.innerHTML = '';
                
                data.forEach(key => {
                    // 상태 결정 로직
                    let status = 'UNKNOWN';
                    if (key.apiKey === 'PENDING') {
                        status = 'PENDING';
                    } else if (key.useYn === 'N') {
                        status = 'EXPIRED';
                    } else if (key.expiresDt && new Date(key.expiresDt) < new Date()) {
                        status = 'EXPIRED';
                    } else if (key.useYn === 'Y') {
                        status = 'ACTIVE';
                    }

                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${key.purpose || '-'}</td>
                        <td>${key.apiKey || '-'}</td>
                        <td>${key.issuedDt ? new Date(key.issuedDt).toLocaleDateString() : '-'}</td>
                        <td>${key.expiresDt ? new Date(key.expiresDt).toLocaleDateString() : '-'}</td>
                        <td><span class="status-${status.toLowerCase()}">${status}</span></td>
                    `;
                    tbody.appendChild(tr);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire('오류', error.message, 'error');
            });
    },

    init: function() {
        this.loadApiKeys();
    }
};

const adminFunctions = {
    currentUserEmail: null, // 현재 로그인한 사용자의 이메일을 저장할 변수

    loadUsers() {
        // 먼저 현재 로그인한 사용자 정보를 가져옴
        fetch('/api/users/current')
            .then(response => response.json())
            .then(data => {
                this.currentUserEmail = data.email;
                this.loadUserData();
            });
    },

    loadUserData() {
        fetch('/api/admin/users')
            .then(response => {
                console.log('Admin users API response:', response);
                return response.json();
            })
            .then(data => {
                console.log('Admin users API data:', data);
                
                const userTbody = document.querySelector('#userTable tbody');
                const apiKeyTbody = document.querySelector('#apiKeyTable tbody');
                userTbody.innerHTML = '';
                apiKeyTbody.innerHTML = '';
                
                // 이미 추가된 이메일 추적을 위한 Set
                const addedEmails = new Set();
                
                data.data.forEach(user => {
                    // 사용자 정보는 중복되지 않게 추가
                    if (!addedEmails.has(user.email)) {
                        addedEmails.add(user.email);
                        
                        const userRow = document.createElement('tr');
                        const isCurrentUser = user.email === this.currentUserEmail;
                        
                        userRow.innerHTML = `
                            <td>${user.user_nm || '-'}</td>
                            <td>${user.email || '-'}</td>
                            <td class="${isCurrentUser ? '' : 'editable-role'}" data-user-id="${user.user_id}">
                                ${user.role_cd === '01' ? '관리자' : '일반사용자'}
                                ${isCurrentUser ? '' : '<button class="btn-edit-role">수정</button>'}
                            </td>
                        `;
                        userTbody.appendChild(userRow);
                    }

                    // API 키 정보는 모두 추가
                    if (user.api_status) {
                        const apiKeyRow = document.createElement('tr');
                        let status = '';
                        if (user.api_status === 'Y' && (!user.expires_dt || new Date(user.expires_dt) > new Date())) {
                            status = '<span class="status-active">Active</span>';
                        } else {
                            status = '<span class="status-expired">Expired</span>';
                        }

                        apiKeyRow.innerHTML = `
                            <td>${user.purpose || '-'}</td>
                            <td>${user.email || '-'}</td>
                            <td class="editable-date" data-api-key="${user.api_key}">
                                ${commonFunctions.formatDate(user.expires_dt) || '-'}
                            </td>
                            <td>${status}</td>
                        `;
                        apiKeyTbody.appendChild(apiKeyRow);
                    }
                });

                this.initializeEventListeners();
            })
            .catch(error => {
                console.error('Admin users API error:', error);
            });
    },

    initializeEventListeners() {
        // 권한 수정 버튼 이벤트
        document.querySelectorAll('.btn-edit-role').forEach(button => {
            button.addEventListener('click', (e) => {
                const cell = e.target.parentElement;
                const userId = cell.dataset.userId;
                const currentRole = cell.textContent.trim() === '관리자' ? '01' : '02';

                Swal.fire({
                    title: '권한 변경',
                    html: `
                        <select id="roleSelect" class="swal2-select">
                            <option value="01" ${currentRole === '01' ? 'selected' : ''}>관리자</option>
                            <option value="02" ${currentRole === '02' ? 'selected' : ''}>일반사용자</option>
                        </select>
                    `,
                    showCancelButton: true,
                    confirmButtonText: '변경',
                    cancelButtonText: '취소'
                }).then((result) => {
                    if (result.isConfirmed) {
                        const newRole = document.getElementById('roleSelect').value;
                        this.updateUserRole(userId, newRole);
                    }
                });
            });
        });

        // 만료일 수정 이벤트
        document.querySelectorAll('.editable-date').forEach(cell => {
            cell.addEventListener('click', (e) => {
                // 클릭된 요소가 td가 아닐 경우 td를 찾아야 함
                const tdElement = e.target.closest('td');
                if (!tdElement) return;
                
                const apiKey = tdElement.dataset.apiKey;
                const currentDate = tdElement.textContent.trim();

                console.log('Clicked element:', tdElement);  // 디버깅용
                console.log('API Key:', apiKey);  // 디버깅용
                
                Swal.fire({
                    title: '만료일 변경',
                    html: `<input type="date" id="expiryDate" class="swal2-input" value="${currentDate}">`,
                    showCancelButton: true,
                    confirmButtonText: '변경',
                    cancelButtonText: '취소'
                }).then((result) => {
                    if (result.isConfirmed) {
                        const newDate = document.getElementById('expiryDate').value;
                        if (apiKey) {  // apiKey가 있을 때만 실행
                            this.updateApiKeyExpiry(apiKey, newDate);
                        } else {
                            console.error('API Key is undefined');
                            Swal.fire('오류', 'API Key를 찾을 수 없습니다.', 'error');
                        }
                    }
                });
            });
        });
    },

    updateUserRole(userId, newRole) {
        fetch('/api/admin/users/role', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                roleCd: newRole
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'SUCCESS') {
                Swal.fire('성공', '권한이 변경되었습니다.', 'success')
                    .then(() => this.loadUserData());
            } else {
                throw new Error(data.message || '권한 변경 실패');
            }
        })
        .catch(error => {
            Swal.fire('오류', error.message, 'error');
        });
    },

    updateApiKeyExpiry(apiKey, newDate) {
        console.log('Updating API key expiry:', { apiKey, newDate });  // 요청 데이터 로그
        
        fetch('/api/admin/keys/expiry', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                apiKey: apiKey,
                expiryDate: newDate
            })
        })
        .then(response => {
            console.log('Update response:', response);  // 응답 로그
            return response.json();
        })
        .then(data => {
            console.log('Update response data:', data);  // 응답 데이터 로그
            if (data.status === 'SUCCESS') {
                Swal.fire('성공', '만료일이 변경되었습니다.', 'success')
                    .then(() => this.loadUserData());
            } else {
                throw new Error(data.message || '만료일 변경 실패');
            }
        })
        .catch(error => {
            console.error('Update error:', error);  // 에러 로그
            Swal.fire('오류', error.message, 'error');
        });
    },

    init() {
        this.loadUsers();
    }
};

// File Upload functions
const uploadFunctions = {
    previewFile() {
        const fileInput = document.getElementById('excelFile');
        const previewRows = document.getElementById('previewRows').value;
        const file = fileInput.files[0];

        if (!file) {
            Swal.fire('오류', '파일을 선택해주세요.', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);
        formData.append('previewRows', previewRows);

        fetch('/api/upload/preview', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'SUCCESS') {
                this.displayPreview(data.data);
                document.querySelector('.preview-section').style.display = 'block';
            } else {
                throw new Error(data.message || '미리보기 실패');
            }
        })
        .catch(error => {
            console.error('Preview error:', error);
            Swal.fire('오류', error.message, 'error');
        });
    },

    displayPreview(previewData) {
        // 헤더 표시
        const thead = document.querySelector('#previewTable thead');
        // thead.innerHTML = '<tr>' + 
        //     previewData.headers.map((_, index) => `<th>Column ${index + 1}</th>`).join('') + 
        //     '</tr>';
        thead.innerHTML = '<tr>' + 
            previewData.headers.map(header => `<th>${header}</th>`).join('') + 
            '</tr>';

        // 데이터 표시
        const tbody = document.querySelector('#previewTable tbody');
        tbody.innerHTML = previewData.data.map(row => 
            '<tr>' + row.map(cell => `<td>${cell}</td>`).join('') + '</tr>'
        ).join('');

        // 전체 행 수 표시
        document.getElementById('totalRows').textContent = 
            `전체 ${previewData.totalRows}행`;

        // 컬럼 정의 섹션 초기화
        const columnDefs = document.getElementById('columnDefinitions');
        columnDefs.innerHTML = '';
        previewData.headers.forEach((header, index) => {
            this.addColumnDefinition(index, header);  // header 값을 전달
        });
    },

    addColumnDefinition(index = null, defaultName = '') {
        const container = document.getElementById('columnDefinitions');
        const div = document.createElement('div');
        div.className = 'column-definition';
        div.innerHTML = `
            <div class="column-definition-row">
                <div class="column-label">컬럼명:</div>
                <input type="text" placeholder="컬럼명" class="column-name" value="${defaultName}">
            </div>
            <div class="column-definition-row">
                <div class="column-label">데이터타입:</div>
                <select class="column-type">
                    <option value="varchar(100)">VARCHAR(100)</option>
                    <option value="text">TEXT</option>
                    <option value="integer">INTEGER</option>
                    <option value="bigint">BIGINT</option>
                    <option value="numeric">NUMERIC</option>
                    <option value="date">DATE</option>
                    <option value="timestamp">TIMESTAMP</option>
                    <option value="boolean">BOOLEAN</option>
                </select>
            </div>
            <div class="column-definition-row">
                <div class="column-label">엑셀 열 번호:</div>
                <input type="number" placeholder="엑셀 열 번호" class="excel-column" 
                       value="${index !== null ? index : ''}" min="0">
            </div>
        `;
        container.appendChild(div);
    },

    startImport() {
        const fileInput = document.getElementById('excelFile');
        const file = fileInput.files[0];
        const tableName = document.getElementById('tableName').value.trim();
        const startRow = parseInt(document.getElementById('startRow').value);
        const endRow = parseInt(document.getElementById('endRow').value);

        // 입력 검증
        if (!this.validateImportInputs(file, tableName, startRow, endRow)) {
            return;
        }

        // 컬럼 정의 수집
        const columns = this.collectColumnDefinitions();
        if (!columns) return;

        const formData = new FormData();
        formData.append('file', file);
        formData.append('request', new Blob([JSON.stringify({
            tableName: tableName,
            columns: columns,
            startRow: startRow,
            endRow: endRow
        })], {
            type: 'application/json'
        }));

        // 데이터 이관 요청
        fetch('/api/upload/import', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'SUCCESS') {
                Swal.fire('성공', `${data.data.processedRows}행이 처리되었습니다.`, 'success');
            } else {
                throw new Error(data.message || '이관 실패');
            }
        })
        .catch(error => {
            console.error('Import error:', error);
            Swal.fire('오류', error.message, 'error');
        });
    },

    validateImportInputs(file, tableName, startRow, endRow) {
        if (!file) {
            Swal.fire('오류', '파일을 선택해주세요.', 'error');
            return false;
        }
        if (!tableName) {
            Swal.fire('오류', '테이블 이름을 입력해주세요.', 'error');
            return false;
        }
        if (isNaN(startRow) || isNaN(endRow) || startRow > endRow) {
            Swal.fire('오류', '올바른 행 범위를 입력해주세요.', 'error');
            return false;
        }
        return true;
    },

    collectColumnDefinitions() {
        const columns = [];
        const definitions = document.querySelectorAll('.column-definition');
        
        for (let def of definitions) {
            const name = def.querySelector('.column-name').value.trim();
            const type = def.querySelector('.column-type').value;
            const excelIndex = parseInt(def.querySelector('.excel-column').value);

            if (!name || isNaN(excelIndex)) {
                Swal.fire('오류', '모든 컬럼 정보를 입력해주세요.', 'error');
                return null;
            }

            columns.push({
                name: name,
                type: type,
                excelIndex: excelIndex
            });
        }
        return columns;
    },

    init() {
        // 파일 선택 시 자동 미리보기
        const fileInput = document.getElementById('excelFile');
        if (fileInput) {
            fileInput.addEventListener('change', () => this.previewFile());
        }
    }
};

const apiTestFunctions = {
    testCodeApi() {
        const requestBody = document.getElementById('requestBody').value;
        let jsonBody;
        
        try {
            jsonBody = JSON.parse(requestBody);
            console.log('Sending API request with body:', JSON.stringify(jsonBody, null, 2));
            
            fetch('/api/v1/code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(jsonBody)
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('apiResponse').textContent = 
                    JSON.stringify(data, null, 2);
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire('오류', 'API 호출 중 오류가 발생했습니다.', 'error');
            });
        } catch (e) {
            console.error('JSON parse error:', e);
            Swal.fire('오류', 'JSON 형식이 올바르지 않습니다.', 'error');
        }
    },

    testDateSearchApi() {
        const requestBody = document.getElementById('dateSearchRequestBody').value;
        let jsonBody;
        
        try {
            jsonBody = JSON.parse(requestBody);
            console.log('Sending date search request with body:', JSON.stringify(jsonBody, null, 2));
            
            fetch('/api/v1/date-search', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(jsonBody)
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('dateSearchResponse').textContent = 
                    JSON.stringify(data, null, 2);
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire('오류', 'API 호출 중 오류가 발생했습니다.', 'error');
            });
        } catch (e) {
            console.error('JSON parse error:', e);
            Swal.fire('오류', 'JSON 형식이 올바르지 않습니다.', 'error');
        }
    },

    testProductSearchApi() {
        const requestBody = document.getElementById('productSearchRequestBody').value;
        let jsonBody;
        
        try {
            jsonBody = JSON.parse(requestBody);
            console.log('Sending product search request with body:', JSON.stringify(jsonBody, null, 2));
            
            fetch('/api/v1/product-search', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(jsonBody)
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('productSearchResponse').textContent = 
                    JSON.stringify(data, null, 2);
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire('오류', 'API 호출 중 오류가 발생했습니다.', 'error');
            });
        } catch (e) {
            console.error('JSON parse error:', e);
            Swal.fire('오류', 'JSON 형식이 올바르지 않습니다.', 'error');
        }
    },

    setupApiKeyHandlers() {
        // 코드 조회 API 키 핸들러
        document.getElementById('apiKey').addEventListener('input', function() {
            const apiKey = this.value.trim();
            const requestBody = document.getElementById('requestBody');
            
            if (apiKey.endsWith('/code')) {
                const jsonBody = {
                    serviceCrtfcKey: ""
                };
                requestBody.value = JSON.stringify(jsonBody, null, 4);
            }
        });

        // 날짜 검색 API 키 핸들러
        document.getElementById('dateSearchApiKey').addEventListener('input', function() {
            const apiKey = this.value.trim();
            const requestBody = document.getElementById('dateSearchRequestBody');
            
            if (apiKey.endsWith('/date-search')) {
                const jsonBody = {
                    serviceCrtfcKey: "",
                    reqDocDt: ""
                };
                requestBody.value = JSON.stringify(jsonBody, null, 4);
            }
        });

        // 제품 검색 API 키 핸들러
        document.getElementById('productSearchApiKey').addEventListener('input', function() {
            const apiKey = this.value.trim();
            const requestBody = document.getElementById('productSearchRequestBody');
            
            if (apiKey.endsWith('/product-search')) {
                const jsonBody = {
                    serviceCrtfcKey: "",
                    reqPrdctNm: "",
                    reqBzentyNm: ""
                };
                requestBody.value = JSON.stringify(jsonBody, null, 4);
            }
        });

        // 페이지 로드 시 textarea 초기화
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('requestBody').value = '';
            document.getElementById('dateSearchRequestBody').value = '';
            document.getElementById('productSearchRequestBody').value = '';
        });
    }
};

// 페이지별 초기화
document.addEventListener('DOMContentLoaded', () => {
    // console.log('Global DOMContentLoaded event fired');
    
    // 로그아웃 버튼 이벤트 리스너 등록 (모든 페이지)
    const logoutBtn = document.querySelector('.btn-danger');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', commonFunctions.logout);
    }

    // 페이지별 초기화
    if (document.querySelector('#loginForm')) {
        loginFunctions.init();
    }
    if (document.querySelector('#signupForm')) {
        signupFunctions.init();
    }
    if (document.querySelector('.main-page')) {
        test01Functions.init();
    }
    if (document.querySelector('.api-keys')) {
        // console.log('API Keys section found - initializing myPageFunctions');
        myPageFunctions.init();
    }
    if (document.querySelector('.admin-panel')) {
        adminFunctions.init();
    }
    if (document.querySelector('.upload-container')) {
        uploadFunctions.init();
    }

    // 페이지 로드 시 API 키 핸들러 설정
    if (document.getElementById('apiKey')) {  // API 테스트 페이지인 경우에만
        apiTestFunctions.setupApiKeyHandlers();
    }
}); 