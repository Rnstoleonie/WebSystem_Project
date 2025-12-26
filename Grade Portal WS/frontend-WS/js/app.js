const API_BASE = "http://localhost:8080/api";

// ----------------- Utility Functions -----------------
function extractUsernameFromToken(token) {
    try {
        // JWT token format: header.payload.signature
        const payload = token.split('.')[1];
        // Decode base64 payload
        const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        const payloadObj = JSON.parse(decodedPayload);
        return payloadObj.sub; // 'sub' contains the username
    } catch (error) {
        console.error('Error extracting username from token:', error);
        return null;
    }
}

function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<tr><td colspan="6">Loading...</td></tr>';
    }
}

function showError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<tr><td colspan="6" style="color: red;">' + message + '</td></tr>';
    }
}

function showSuccessMessage(message) {
    alert(message);
}

function showInfoMessage(message) {
    // Create a temporary info message
    const infoDiv = document.createElement('div');
    infoDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #3498db;
        color: white;
        padding: 15px;
        border-radius: 5px;
        z-index: 1000;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    `;
    infoDiv.textContent = message;
    document.body.appendChild(infoDiv);
    
    setTimeout(() => {
        document.body.removeChild(infoDiv);
    }, 3000);
}

// ----------------- API Helper Functions -----------------
function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}

async function apiCall(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                ...getAuthHeaders(),
                ...options.headers
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                // Token expired or invalid
                localStorage.removeItem("token");
                localStorage.removeItem("role");
                alert("Session expired. Please login again.");
                showSection('loginSection');
                return null;
            }
            const errorData = await response.text();
            throw new Error(errorData || `HTTP error! status: ${response.status}`);
        }

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        console.error("API call failed:", error);
        throw error;
    }
}

// ----------------- Section Management -----------------
function showSection(sectionId) {
    // Hide all sections
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => section.classList.remove('active'));

    // Show the specified section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }
}

function toggleSection(contentId) {
    const content = document.getElementById(contentId);
    if (content) {
        content.classList.toggle('active');
    }
}

// ----------------- Initialization -----------------
document.addEventListener("DOMContentLoaded", () => {
    // Check if user is already logged in
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (token && role) {
        // User is logged in, show appropriate dashboard
        if (role === "ADMIN") {
            showSection('adminSection');
            loadTeachers();
            loadRequests();
        } else if (role === "TEACHER") {
            showSection('teacherSection');
            loadStudents();
            loadSubjects();
        } else if (role === "STUDENT") {
            showSection('studentSection');
            loadMyGrades();
        }
    } else {
        // Show login section
        showSection('loginSection');
    }

    // Setup logout buttons
    const logoutButtons = document.querySelectorAll("#logoutBtn");
    logoutButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            localStorage.removeItem("token");
            localStorage.removeItem("role");
            showInfoMessage("Logged out successfully");
            showSection('loginSection');
        });
    });

    // Setup login form
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            const errorMsg = document.getElementById("errorMsg");

            // Clear previous error
            if (errorMsg) errorMsg.textContent = "";

            try {
                const res = await fetch(`${API_BASE}/auth/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password })
                });

                if (!res.ok) {
                    if (res.status === 401) {
                        throw new Error("Invalid username or password");
                    } else {
                        throw new Error("Login failed. Please try again.");
                    }
                }

                const data = await res.json();

                localStorage.setItem("token", data.token);
                localStorage.setItem("role", data.user.role);

                showSuccessMessage("LOGIN SUCCESS!");
                if (data.user.role === "ADMIN") {
                    showSection('adminSection');
                    loadTeachers();
                    loadRequests();
                } else if (data.user.role === "TEACHER") {
                    showSection('teacherSection');
                    loadStudents();
                    loadSubjects();
                } else if (data.user.role === "STUDENT") {
                    showSection('studentSection');
                    loadMyGrades();
                }

            } catch (err) {
                if (errorMsg) errorMsg.textContent = err.message;
                console.error(err);
            }
        });
    }

    // Setup signup form
    const signupForm = document.getElementById("signupForm");
    if (signupForm) {
        signupForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            const firstName = document.getElementById("firstName").value;
            const lastName = document.getElementById("lastName").value;
            const role = document.getElementById("role").value;

            try {
                const res = await fetch(`${API_BASE}/auth/signup`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password, firstName, lastName, role })
                });

                const message = await res.text();
                document.getElementById("message").textContent = message;
                showInfoMessage("Signup request submitted successfully!");

            } catch (err) {
                document.getElementById("message").textContent = "Signup failed";
                console.error(err);
            }
        });
    }

    // Setup form event listeners
    const assignForm = document.getElementById("assignTeacherForm");
    if (assignForm) {
        assignForm.addEventListener("submit", assignTeacher);
    }

    const addStudentForm = document.getElementById("addStudentForm");
    if (addStudentForm) {
        addStudentForm.addEventListener("submit", addStudent);
    }

    const assignGradeForm = document.getElementById("assignGradeForm");
    if (assignGradeForm) {
        assignGradeForm.addEventListener("submit", assignGrade);
    }

}

// ----------------- Admin Functions -----------------
async function loadTeachers() {
    const tbody = document.querySelector("#teachersTable tbody");
    if (!tbody) return;

    showLoading("teachersTable");

    try {
        const teachers = await apiCall(`${API_BASE}/users/teachers`);

        tbody.innerHTML = "";
        teachers.forEach(t => {
            const tr = document.createElement("tr");
            const statusClass = t.status === 'APPROVED' ? 'active' : 'pending';
            tr.innerHTML = '<td>' + t.id + '</td>' +
                '<td>' + t.firstName + ' ' + t.lastName + '</td>' +
                '<td>' + t.username + '</td>' +
                '<td><span class="status-badge ' + statusClass + '">' + (t.status || 'Active') + '</span></td>' +
                '<td>' + (t.assignedClass || 'Not Assigned') + '</td>';
            tbody.appendChild(tr);
        });

        // Populate teacher select for assignment
        const select = document.getElementById("teacherId");
        if (select) {
            select.innerHTML = '<option value="">Choose a teacher...</option>';
            teachers.forEach(t => {
                select.innerHTML += `<option value="${t.id}">${t.firstName} ${t.lastName}</option>`;
            });
        }
    } catch (error) {
        console.error("Error loading teachers:", error);
        showError("teachersTable", "Failed to load teachers");
    }
}

async function loadRequests() {
    const tbody = document.querySelector("#requestsTable tbody");
    if (!tbody) return;

    showLoading("requestsTable");

    try {
        const requests = await apiCall(`${API_BASE}/users/pending`);

        tbody.innerHTML = "";
        requests.forEach(r => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${r.id}</td>
                <td>${r.username}</td>
                <td>${r.role}</td>
                <td>${r.firstName} ${r.lastName}</td>
                <td>
                    <button onclick="approveRequest(${r.id})" class="btn btn-success">Approve</button>
                    <button onclick="declineRequest(${r.id})" class="btn btn-danger">Decline</button>
                    <button onclick="deleteRequest(${r.id})" class="btn btn-warning">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Error loading requests:", error);
        showError("requestsTable", "Failed to load requests");
    }
}

async function approveRequest(id) {
    try {
        await apiCall(`${API_BASE}/users/${id}/approve`, { method: "PUT" });
        showInfoMessage("Request approved successfully!");
        loadRequests();
        loadTeachers();
    } catch (error) {
        console.error("Error approving request:", error);
        alert("Failed to approve request");
    }
}

async function declineRequest(id) {
    try {
        await apiCall(`${API_BASE}/users/${id}/decline`, { method: "PUT" });
        showInfoMessage("Request declined successfully!");
        loadRequests();
    } catch (error) {
        console.error("Error declining request:", error);
        alert("Failed to decline request");
    }
}

async function deleteRequest(id) {
    if (confirm("Are you sure you want to delete this request?")) {
        try {
            await apiCall(`${API_BASE}/users/${id}`, { method: "DELETE" });
            showInfoMessage("Request deleted successfully!");
            loadRequests();
        } catch (error) {
            console.error("Error deleting request:", error);
            alert("Failed to delete request");
        }
    }
}

async function assignTeacher(e) {
    e.preventDefault();
    const teacherId = document.getElementById("teacherId").value;
    const className = document.getElementById("className").value;

    if (!teacherId || !className) {
        alert("Please fill in all fields");
        return;
    }

    try {
        await apiCall(`${API_BASE}/users/${teacherId}/assign`, {
            method: "PUT",
            body: JSON.stringify({ className })
        });
        showInfoMessage("Teacher assigned successfully!");
        document.getElementById("className").value = "";
        document.getElementById("teacherId").value = "";
        loadTeachers();
    } catch (error) {
        console.error("Error assigning teacher:", error);
        alert("Failed to assign teacher");
    }
}

// ----------------- Teacher Functions -----------------
async function loadStudents() {
    const tbody = document.querySelector("#studentsTable tbody");
    if (!tbody) return;

    showLoading("studentsTable");

    try {
        const students = await apiCall(`${API_BASE}/students`);

        tbody.innerHTML = "";
        students.forEach(s => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${s.id}</td>
                <td>${s.name}</td>
                <td>${s.section}</td>
                <td>
                    <button onclick="viewStudentGrades(${s.id})" class="btn btn-primary">View Grades</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Populate student select
        const select = document.getElementById("studentId");
        if (select) {
            select.innerHTML = '<option value="">Choose student...</option>';
            students.forEach(s => {
                select.innerHTML += `<option value="${s.id}">${s.name}</option>`;
            });
        }
    } catch (error) {
        console.error("Error loading students:", error);
        showError("studentsTable", "Failed to load students");
    }
}

async function loadSubjects() {
    try {
        const subjects = await apiCall(`${API_BASE}/subjects`);

        const select = document.getElementById("subjectId");
        if (select) {
            select.innerHTML = '<option value="">Choose subject...</option>';
            subjects.forEach(s => {
                select.innerHTML += `<option value="${s.id}">${s.name}</option>`;
            });
        }
    } catch (error) {
        console.error("Error loading subjects:", error);
    }
}

async function addStudent(e) {
    e.preventDefault();
    const name = document.getElementById("studentName").value;
    const section = document.getElementById("studentSection").value;

    if (!name || !section) {
        alert("Please fill in all fields");
        return;
    }

    try {
        await apiCall(`${API_BASE}/students`, {
            method: "POST",
            body: JSON.stringify({ name, section })
        });
        showInfoMessage("Student added successfully!");
        document.getElementById("studentName").value = "";
        document.getElementById("studentSection").value = "";
        loadStudents();
    } catch (error) {
        console.error("Error adding student:", error);
        alert("Failed to add student");
    }
}

async function assignGrade(e) {
    e.preventDefault();
    const studentId = document.getElementById("studentId").value;
    const subjectId = document.getElementById("subjectId").value;
    const gradeValue = document.getElementById("gradeValue").value;

    if (!studentId || !subjectId || !gradeValue) {
        alert("Please fill in all fields");
        return;
    }

    if (gradeValue < 0 || gradeValue > 100) {
        alert("Grade must be between 0 and 100");
        return;
    }

    try {
        await apiCall(`${API_BASE}/grades`, {
            method: "POST",
            body: JSON.stringify({ 
                student: { id: studentId }, 
                subject: { id: subjectId }, 
                gradeValue: parseInt(gradeValue),
                dateAssigned: new Date().toISOString().split('T')[0]
            })
        });
        showInfoMessage("Grade assigned successfully!");
        document.getElementById("studentId").value = "";
        document.getElementById("subjectId").value = "";
        document.getElementById("gradeValue").value = "";
    } catch (error) {
        console.error("Error assigning grade:", error);
        alert("Failed to assign grade");
    }
}

async function viewStudentGrades(studentId) {
    try {
        const grades = await apiCall(`${API_BASE}/grades/student/${studentId}`);
        
        let message = `Grades for student ID ${studentId}:\n\n`;
        grades.forEach(g => {
            message += `${g.subject.name}: ${g.gradeValue}\n`;
        });
        
        alert(message);
    } catch (error) {
        console.error("Error loading student grades:", error);
        alert("Failed to load student grades");
    }
}

// ----------------- Student Functions -----------------
async function loadMyGrades() {
    const tbody = document.querySelector("#gradesTable tbody");
    if (!tbody) return;

    showLoading("gradesTable");

    // Get current user info from localStorage (username from token)
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Please log in first");
        showSection('loginSection');
        return;
    }

    // Extract username from JWT token
    const username = extractUsernameFromToken(token);

    try {
        // Get current user info
        const currentUser = await apiCall(`${API_BASE}/users/current?username=${username}`);

        if (!currentUser || currentUser.role !== 'STUDENT') {
            alert("Access denied. Only students can view this page.");
            showSection('loginSection');
            return;
        }

        // Get student record for this user
        const student = await apiCall(`${API_BASE}/students/user/${currentUser.id}`);

        if (student) {
            // Get detailed report card with statistics
            const reportCard = await apiCall(`${API_BASE}/grades/student/${student.id}/report`);

            // Display grades table
            tbody.innerHTML = "";

            if (reportCard.grades && reportCard.grades.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4">No grades available</td></tr>';
            } else if (reportCard.grades) {
                reportCard.grades.forEach(g => {
                    const status = g.gradeValue >= 60 ? 'Pass' : 'Fail';
                    const statusClass = g.gradeValue >= 60 ? 'pass' : 'fail';
                    const tr = document.createElement("tr");
                    tr.innerHTML = `
                        <td>${g.subject.name}</td>
                        <td><strong>${g.gradeValue}</strong></td>
                        <td><span class="status-badge ${statusClass}">${status}</span></td>
                        <td>${g.dateAssigned || 'N/A'}</td>
                    `;
                    tbody.appendChild(tr);
                });
            } else {
                tbody.innerHTML = '<tr><td colspan="4">No grades available</td></tr>';
            }

            // Display statistics
            displayGradeStatistics(reportCard);
        } else {
            tbody.innerHTML = '<tr><td colspan="4">Student record not found</td></tr>';
        }
    } catch (error) {
        console.error("Error loading grades:", error);
        tbody.innerHTML = '<tr><td colspan="4">Error loading grades</td></tr>';
    }
}

function displayGradeStatistics(reportCard) {
    const statsContainer = document.querySelector("#gradeStats");
    if (!statsContainer || !reportCard) return;
    
    const { totalGrades, passedGrades, failedGrades, averageGrade, overallStatus } = reportCard;
    
    // Calculate percentage passed
    const passPercentage = totalGrades > 0 ? Math.round((passedGrades / totalGrades) * 100) : 0;
    
    statsContainer.innerHTML = `
        <div class="stat-card average">
            <h4>Average Grade</h4>
            <div class="stat-value blue">${averageGrade || 0}</div>
        </div>
        <div class="stat-card passed">
            <h4>Subjects Passed</h4>
            <div class="stat-value green">${passedGrades || 0}/${totalGrades || 0}</div>
            <small>(${passPercentage}% pass rate)</small>
        </div>
        <div class="stat-card status">
            <h4>Overall Status</h4>
            <div class="stat-value orange">${overallStatus || 'N/A'}</div>
        </div>
    `;
}

// ----------------- Auto-refresh data every 30 seconds for admin and teacher sections -----------------
setInterval(() => {
    const activeSection = document.querySelector('.section.active');
    if (activeSection) {
        if (activeSection.id === 'adminSection') {
            loadTeachers();
            loadRequests();
        } else if (activeSection.id === 'teacherSection') {
            loadStudents();
        }
    }
}, 30000);
