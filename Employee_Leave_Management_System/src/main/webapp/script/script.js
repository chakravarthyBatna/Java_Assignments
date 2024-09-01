document.addEventListener("DOMContentLoaded", function () {

    const employeeDetails = document.getElementById('employee-details');
    if (employeeDetails) {
        const container = document.createElement('div');
        container.id = 'leave-summary-cards';
        employeeDetails.appendChild(container);
        console.log('Created and appended leave-summary-cards container');
    }

    fetchLeaveSummary();
    const dashboardLink = document.getElementById('dashboard');
    dashboardLink.addEventListener('click', function (event) {
        event.preventDefault();
        fetchLeaveSummary();
    });

    async function fetchLeaveSummary() {
        try {
            const employeeData = await fetchEmployeeData('http://localhost:8080/api/login');
            const empId = employeeData.empId;

            const response = await fetch(`http://localhost:8080/api/employee/leave/summary?empId=${empId}`);
            const data = await response.json();

            const container = document.getElementById('leave-summary-cards');
            if (!container) {
                console.error('Container element not found');
                return;
            }
            container.innerHTML = '';

            data.forEach(item => {
                const card = document.createElement('div');
                card.className = 'leave-card';

                card.innerHTML = `
                <div class="card-title">${item.leaveType}</div>
                <div class="card-info available-leaves">
                    <span>Available:</span> ${item.pendingLeaves}
                </div>
                <div class="card-info used-leaves">
                    <span>Used:</span> ${item.totalLeavesTaken}
                </div>
            `;

                container.appendChild(card);
            });
        } catch (error) {
            console.error('Error fetching leave summary:', error);
        }
    }

    async function fetchEmployeeData(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching data:', error);
            throw error; // Re-throw the error if you want to handle it later
        }
    }



    // Fetch and display profile name and initial
    fetch("http://localhost:8080/api/login")
        .then(response => response.json())
        .then(data => {
            const profileName = document.getElementById("profile-name");
            const profilePic = document.getElementById("profile-pic");
            const employeeName = data.empName;
            profileName.textContent = employeeName;
            profilePic.textContent = employeeName.charAt(0).toUpperCase();
        })
        .catch(error => {
            console.error("Error fetching user data:", error);
        });

    const profilePic = document.getElementById("profile-pic");
    const profileName = document.getElementById("profile-name");
    //    const employeeDetails = document.getElementById("employee-details");

    async function fetchAndDisplayEmployeeData() {
        try {
            const data = await fetchEmployeeData("http://localhost:8080/api/login");

            // Construct HTML for employee details
            const detailsHtml = `
               <div class="profile-section-info">
                   <div class="info-profile-pic" style="font-size: 40px;">${data.empName.charAt(0).toUpperCase()}</div>
                   <div class="info-profile-name">${data.empName}</div>
               </div>
               <h3>My Details</h3>
               <table class="table">
                   <tbody>
                       <tr>
                           <td><strong>Date of Birth:</strong></td>
                           <td>${new Date(data.empDateOfBirth).toLocaleDateString()}</td>
                       </tr>
                       <tr>
                           <td><strong>Phone Number:</strong></td>
                           <td>${data.phoneNumber}</td>
                       </tr>
                       <tr>
                           <td><strong>Email:</strong></td>
                           <td>${data.email}</td>
                       </tr>
                   </tbody>
               </table>
               <h3>My Manager Details</h3>
               <table class="table">
                   <tbody>
                       <tr>
                           <td><strong>Manager Name:</strong></td>
                           <td>${data.managerName}</td>
                       </tr>
                       <tr>
                           <td><strong>Manager Phone Number:</strong></td>
                           <td>${data.managerPhoneNumber}</td>
                       </tr>
                       <tr>
                           <td><strong>Manager Email:</strong></td>
                           <td>${data.managerEmailId}</td>
                       </tr>
                   </tbody>
               </table>
           `;

            const employeeDetails = document.getElementById("employee-details");
            employeeDetails.innerHTML = detailsHtml;
        } catch (error) {
            console.error("Error fetching and displaying employee data:", error);
        }
    }


    // Add event listeners to profile pic and profile name
    profilePic.addEventListener("click", fetchAndDisplayEmployeeData);
    profileName.addEventListener("click", fetchAndDisplayEmployeeData);

    // Event listener for "My Team Leaves" button
    document.getElementById('my-team-leaves').addEventListener('click', function () {
        const employeeDetailsDiv = document.getElementById('employee-details');
        employeeDetailsDiv.innerHTML = ''; // Clear previous content

        // Create container for tabs and Apply for Leave button
        const headerContainer = document.createElement('div');
        headerContainer.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-3');

        // Create the tabs
        const tabsContainer = document.createElement('ul');
        tabsContainer.classList.add('nav', 'nav-tabs', 'mb-3', 'flex-grow-1'); // Flex-grow to align to left

        const tabs = [
            { id: 'summary-tab', text: 'Summary', callback: () => { displayMyTeamSummary() } },
            {
                id: 'pending-tab', text: 'Pending Approval', callback: () => {
                    fetchTeamLeaveRequests('PENDING');
                }
            },
            {
                id: 'leaves-tab', text: 'Leaves', callback: () => {
                    // Show the leaves tab content with status filter
                    fetchTeamLeaveRequests('ALL_EXCLUDE_PENDING');
                }
            }
        ];

        tabs.forEach(tab => {
            const tabElement = document.createElement('li');
            tabElement.classList.add('nav-item');

            const tabLink = document.createElement('a');
            tabLink.classList.add('nav-link');
            tabLink.href = '#';
            tabLink.textContent = tab.text;
            tabLink.addEventListener('click', function (e) {
                e.preventDefault();
                // Remove active class from all tabs
                tabsContainer.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
                // Add active class to the clicked tab
                tabLink.classList.add('active');
                // Call the tab's callback function
                tab.callback();
            });

            if (tab.id === 'leaves-tab') {
                tabLink.classList.add('active'); // Set "Leaves" tab as active by default
            }

            tabElement.appendChild(tabLink);
            tabsContainer.appendChild(tabElement);
        });

        headerContainer.appendChild(tabsContainer);

        // Create and add the "Apply for Leave" button
        const applyButton = document.createElement('button');
        applyButton.textContent = 'Apply for Leave';
        applyButton.classList.add('btn', 'btn-primary', 'apply-btn');
        applyButton.addEventListener('click', function () {
            // Fetch user information first
            fetch('http://localhost:8080/api/login')
                .then(response => response.json())
                .then(user => {
                    if (user.role === 'ADMIN') {
                        alert("You're an admin, you don't need any leave.");
                    } else {
                        // Fetch leave types if the user is not an ADMIN
                        fetch(`http://localhost:8080/api/leavetypes?gender=${user.gender}`)
                            .then(response => response.json())
                            .then(leaveTypes => {
                                // Populate the leave type dropdown
                                const leaveTypeSelect = document.getElementById('leaveTypeSelect');
                                leaveTypeSelect.innerHTML = ''; // Clear previous options
                                leaveTypes.forEach(leaveType => {
                                    const option = document.createElement('option');
                                    option.value = leaveType.leaveTypeId;
                                    option.textContent = leaveType.leaveType;
                                    leaveTypeSelect.appendChild(option);
                                });

                                // Show the modal for applying leave
                                const applyLeaveModal = new bootstrap.Modal(document.getElementById('applyLeaveModal'));
                                applyLeaveModal.show();
                            })
                            .catch(error => console.error('Error fetching leave types:', error));
                    }
                })
                .catch(error => console.error('Error fetching user information:', error));
        });

        const headerRightContainer = document.createElement('div'); // Right-aligned container
        headerRightContainer.classList.add('d-flex', 'align-items-center');
        headerRightContainer.appendChild(applyButton);

        headerContainer.appendChild(headerRightContainer);
        employeeDetailsDiv.appendChild(headerContainer);

        // Function to fetch and display the leave requests
        function fetchTeamLeaveRequests(status) {
            fetch(`http://localhost:8080/api/employee/my-team-leave?status=${status}`)
                .then(response => response.json())
                .then(data => {
                    // Clear previous content, but retain header
                    employeeDetailsDiv.innerHTML = '';
                    employeeDetailsDiv.appendChild(headerContainer);

                    // Remove existing status filter dropdown if present in non-"Leaves" tabs
                    const existingFilter = document.querySelector('.get-leaves-dropdown');
                    if (existingFilter && status !== 'ALL_EXCLUDE_PENDING') {
                        existingFilter.remove();
                    }

                    if (status === 'ALL_EXCLUDE_PENDING') {
                        // Create and add the status filter dropdown if in "Leaves" tab
                        const statusFilter = document.createElement('select');
                        statusFilter.classList.add('form-select', 'w-auto', 'mb-3', 'get-leaves-dropdown');
                        const statuses = [
                            { value: 'ALL_EXCLUDE_PENDING', text: 'All Requests' },
                            { value: 'APPROVED', text: 'Approved' },
                            { value: 'REJECTED', text: 'Rejected' },
                            { value: 'CANCELLED', text: 'Cancelled' }
                        ];
                        statuses.forEach(statusOption => {
                            const option = document.createElement('option');
                            option.value = statusOption.value;
                            option.textContent = statusOption.text;
                            if (statusOption.value === status) {
                                option.selected = true;
                            }
                            statusFilter.appendChild(option);
                        });

                        statusFilter.addEventListener('change', function () {
                            fetchTeamLeaveRequests(this.value); // Fetch data based on selected status
                        });

                        headerRightContainer.appendChild(statusFilter); // Add status filter to the right container
                    }

                    // Create the table
                    const table = document.createElement('table');
                    table.classList.add('table', 'table-striped', 'table-hover'); // Added 'table-hover' for hover effect

                    const thead = document.createElement('thead');
                    const headerRow = document.createElement('tr');
                    const headers = ['Employee Name', 'Leave Type', 'From Date', 'To Date', 'Total Days', 'Status', 'Actions'];
                    headers.forEach(headerText => {
                        const th = document.createElement('th');
                        th.textContent = headerText;
                        headerRow.appendChild(th);
                    });
                    thead.appendChild(headerRow);
                    table.appendChild(thead);

                    const tbody = document.createElement('tbody');
                    data.forEach(leaveRequest => {
                        const row = document.createElement('tr');

                        const empNameCell = document.createElement('td');
                        empNameCell.textContent = leaveRequest.empName;
                        row.appendChild(empNameCell);

                        const leaveTypeCell = document.createElement('td');
                        leaveTypeCell.textContent = leaveRequest.leaveType;
                        row.appendChild(leaveTypeCell);

                        const fromDateCell = document.createElement('td');
                        fromDateCell.textContent = new Date(leaveRequest.fromDate).toLocaleDateString();
                        row.appendChild(fromDateCell);

                        const toDateCell = document.createElement('td');
                        toDateCell.textContent = new Date(leaveRequest.toDate).toLocaleDateString();
                        row.appendChild(toDateCell);

                        const totalDaysCell = document.createElement('td');
                        totalDaysCell.textContent = leaveRequest.totalNoOfDays;
                        row.appendChild(totalDaysCell);

                        const statusCell = document.createElement('td');
                        statusCell.textContent = leaveRequest.leaveRequestStatus;
                        row.appendChild(statusCell);

                        const actionsCell = document.createElement('td');

                        if (leaveRequest.leaveRequestStatus === 'PENDING') {
                            const approveButton = document.createElement('button');
                            approveButton.textContent = 'Approve';
                            approveButton.classList.add('btn', 'btn-success', 'mr-2');
                            approveButton.addEventListener('click', () => {
                                fetch(`http://localhost:8080/api/employee/my-team-leave?approveOrReject=APPROVED&leaveRequestId=${leaveRequest.leaveRequestId}`, {
                                    method: 'POST'
                                })
                                    .then(response => response.json())
                                    .then(result => {
                                        if (result) {
                                            // Refresh the data
                                            fetchTeamLeaveRequests('PENDING');
                                        }
                                    })
                                    .catch(error => console.error('Error approving leave request:', error));
                            });

                            const rejectButton = document.createElement('button');
                            rejectButton.textContent = 'Reject';
                            rejectButton.classList.add('btn', 'btn-danger', 'reject-btn');
                            rejectButton.addEventListener('click', () => {
                                fetch(`http://localhost:8080/api/employee/my-team-leave?approveOrReject=REJECTED&leaveRequestId=${leaveRequest.leaveRequestId}`, {
                                    method: 'POST'
                                })
                                    .then(response => response.json())
                                    .then(result => {
                                        if (result) {
                                            // Refresh the data
                                            fetchTeamLeaveRequests('PENDING');
                                        }
                                    })
                                    .catch(error => console.error('Error rejecting leave request:', error));
                            });

                            actionsCell.appendChild(approveButton);
                            actionsCell.appendChild(rejectButton);
                        }

                        const detailsButton = document.createElement('button');
                        detailsButton.textContent = 'Details';
                        detailsButton.classList.add('btn', 'btn-info');
                        detailsButton.addEventListener('click', () => {
                            // Show leave request details in a modal
                            const modalTitle = document.getElementById('leaveDetailsModalLabel');
                            const modalBody = document.getElementById('leaveDetailsModalBody');

                            modalTitle.textContent = `Leave Details for ${leaveRequest.empName}`;
                            modalBody.innerHTML = `
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Employee Name:</strong>
                                   ${leaveRequest.empName}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Type:</strong>
                                   ${leaveRequest.leaveType}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Reason:</strong>
                                   ${leaveRequest.leaveReason}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">From Date:</strong>
                                   ${new Date(leaveRequest.fromDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">To Date:</strong>
                                   ${new Date(leaveRequest.toDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Application:</strong>
                                   ${new Date(leaveRequest.dateOfApplication).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Total Days:</strong>
                                   ${leaveRequest.totalNoOfDays}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Status:</strong>
                                   ${leaveRequest.leaveRequestStatus}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Approved:</strong>
                                   ${new Date(leaveRequest.dateOfApproved).toLocaleDateString()}
                               </p>
                           `;


                            const leaveDetailsModal = new bootstrap.Modal(document.getElementById('leaveDetailsModal'));
                            leaveDetailsModal.show();
                        });

                        actionsCell.appendChild(detailsButton);
                        row.appendChild(actionsCell);

                        tbody.appendChild(row);
                    });

                    table.appendChild(tbody);
                    employeeDetailsDiv.appendChild(table);
                })
                .catch(error => console.error('Error fetching leave requests:', error));
        }

        // Display team summary
        function displayMyTeamSummary() {
            const summaryContainer = document.createElement('div');
            summaryContainer.id = 'summary-container';

            employeeDetailsDiv.innerHTML = ''; // Clear previous content
            employeeDetailsDiv.appendChild(headerContainer);
            employeeDetailsDiv.appendChild(summaryContainer);

            fetch('http://localhost:8080/api/employee/leave/summary')
                .then(response => response.json())
                .then(data => {
                    summaryContainer.innerHTML = ''; // Clear any previous content

                    for (const [employee, leaveSummaries] of Object.entries(data)) {
                        // Create a card for each employee
                        const employeeCard = document.createElement('div');
                        employeeCard.classList.add('employee-card');

                        // Extract employee name from the key (assuming the name is inside the key)
                        const employeeName = employee.match(/empName='([^']+)'/)[1];

                        // Create employee name element and center it
                        const nameElement = document.createElement('h2');
                        nameElement.classList.add('employee-name');
                        nameElement.innerText = employeeName;
                        employeeCard.appendChild(nameElement);

                        // Create leave summary container
                        const leaveSummaryContainer = document.createElement('div');
                        leaveSummaryContainer.classList.add('leave-summary-container');

                        leaveSummaries.forEach(summary => {
                            const leaveItem = document.createElement('div');
                            leaveItem.classList.add('leave-item');

                            // Create leave type element
                            const leaveTypeElement = document.createElement('strong');
                            leaveTypeElement.classList.add('leave-type');
                            leaveTypeElement.innerText = summary.leaveType;

                            // Create available and used data
                            const leaveDataElement = document.createElement('span');
                            leaveDataElement.classList.add('leave-data');
                            leaveDataElement.innerHTML = `Available: ${summary.pendingLeaves} <br> Used: ${summary.totalLeavesTaken}`;

                            // Add leave type and data to the leave item
                            leaveItem.appendChild(leaveTypeElement);
                            leaveItem.appendChild(leaveDataElement);

                            // Add the leave item to the leave summary container
                            leaveSummaryContainer.appendChild(leaveItem);
                        });

                        // Add the leave summary container to the employee card
                        employeeCard.appendChild(leaveSummaryContainer);

                        // Add the employee card to the summary container
                        summaryContainer.appendChild(employeeCard);
                    }
                })
                .catch(error => {
                    console.error('Error fetching employee leave summary:', error);
                });
        }


        // Trigger default tab content
        fetchTeamLeaveRequests('ALL_EXCLUDE_PENDING');
    });



    // Modal structure to be added to your HTML:
    const modalHtml = `
        <div class="modal fade" id="leaveDetailsModal" tabindex="-1" aria-labelledby="leaveDetailsModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="leaveDetailsModalLabel">Leave Details</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="leaveDetailsModalBody">
                        <!-- Leave details will be inserted here -->
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.getElementById('applyLeaveForm').addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent form submission

        const fromDateInput = document.getElementById('fromDate');
        const toDateInput = document.getElementById('toDate');
        const fromDate = new Date(fromDateInput.value);
        const toDate = new Date(toDateInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Normalize time to compare dates

        let errorMessage = '';

        // Check if the dates are in the past
        if (fromDate < today) {
            errorMessage += 'From Date must be today or later.\n';
        }
        if (toDate < today) {
            errorMessage += 'To Date must be today or later.\n';
        }
        // Check if From Date is after To Date
        if (fromDate > toDate) {
            errorMessage += 'From Date cannot be later than To Date.\n';
        }

        if (errorMessage) {
            alert(errorMessage); // Show error message
            return; // Stop form submission
        } else {
            // Form is valid, proceed with submission
            console.log('Form is valid. Submitting leave application...');

            const leaveTypeId = document.getElementById('leaveTypeSelect').value;
            const leaveReason = document.getElementById('leaveReason').value;
            const fromDate = document.getElementById('fromDate').value;
            const toDate = document.getElementById('toDate').value;
            const dateOfApplication = new Date().toISOString().split('T')[0]; // Current date
            const leaveRequestStatus = 'PENDING';

            const leaveRequest = {
                leaveTypeId,
                leaveReason,
                fromDate,
                toDate,
                dateOfApplication,
                leaveRequestStatus
            };

          fetch('http://localhost:8080/api/employee/leave', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify(leaveRequest)
          })
          .then(response => {
              if (!response.ok) {
                  return response.json().then(errorData => {
                      throw new Error(errorData.message || 'Unknown error occurred');
                  });
              }
              return response.json();
          })
          .then(result => {
              if (result) {
                  // Hide the modal and refresh the leave data
                  const applyLeaveModal = bootstrap.Modal.getInstance(document.getElementById('applyLeaveModal'));
                  applyLeaveModal.hide();
                  document.getElementById('my-leaves').click();
              }
          })
          .catch(error => {
              console.error('Error applying for leave:', error);
              // Show error dialog with the message
              showErrorDialog(error.message);
          });

        }
         function showErrorDialog(message) {
                const errorModal = document.getElementById('errorModal');
                const errorMessage = document.getElementById('errorMessage');
                errorMessage.textContent = message;
                const modal = new bootstrap.Modal(errorModal);
                modal.show();
            }
    });

    // Append modal to the body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    // My Leaves Details
    document.getElementById('my-leaves').addEventListener('click', function () {
        fetchLeaveRequests('ALL'); // Initial fetch with ALL status


        function fetchLeaveRequests(status) {
            fetch(`http://localhost:8080/api/employee/leave?status=${status}`)
                .then(response => response.json())
                .then(data => {
                    const employeeDetails = document.getElementById('employee-details');
                    employeeDetails.innerHTML = ''; // Clear previous content

                    // Create a container for the title and button
                    const headerContainer = document.createElement('div');
                    headerContainer.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-3');

                    // Create and add the title
                    const title = document.createElement('h3');
                    title.textContent = 'My Leaves';
                    title.classList.add('text-center', 'w-100', 'font-weight-bold');
                    headerContainer.appendChild(title);

                    // Create and add the "Apply for Leave" button
                    const applyButton = document.createElement('button');
                    applyButton.textContent = 'Apply for Leave';
                    applyButton.classList.add('btn', 'btn-primary', 'apply-btn');
                    applyButton.style.float = 'right';
                    applyButton.addEventListener('click', function () {
                        // Fetch user information
                        fetch('http://localhost:8080/api/login')
                            .then(response => response.json())
                            .then(user => {
                                if (user.role === 'ADMIN') {
                                    // Show prompt message if the user is an ADMIN
                                    alert("You're an admin, you don't need any leave.");
                                } else {
                                    // Fetch leave types if the user is not an ADMIN
                                    fetch(`http://localhost:8080/api/leavetypes?gender=${user.gender}`)
                                        .then(response => response.json())
                                        .then(leaveTypes => {
                                            // Populate the leave type dropdown
                                            const leaveTypeSelect = document.getElementById('leaveTypeSelect');
                                            leaveTypeSelect.innerHTML = ''; // Clear previous options
                                            leaveTypes.forEach(leaveType => {
                                                const option = document.createElement('option');
                                                option.value = leaveType.leaveTypeId;
                                                option.textContent = leaveType.leaveType;
                                                leaveTypeSelect.appendChild(option);
                                            });

                                            // Show the modal for applying leave
                                            const applyLeaveModal = new bootstrap.Modal(document.getElementById('applyLeaveModal'));
                                            applyLeaveModal.show();
                                        })
                                        .catch(error => console.error('Error fetching leave types:', error));
                                }
                            })
                            .catch(error => console.error('Error fetching user information:', error));
                    });

                    headerContainer.appendChild(applyButton);

                    // Create the status filter dropdown
                    const statusFilter = document.createElement('select');
                    statusFilter.classList.add('form-select', 'w-auto', 'mr-2');
                    const statuses = [
                        { value: 'ALL', text: 'All Requests' },
                        { value: 'APPROVED', text: 'Approved' },
                        { value: 'REJECTED', text: 'Rejected' },
                        { value: 'PENDING', text: 'Pending' },
                        { value: 'CANCELLED', text: 'Cancelled' }
                    ];
                    statuses.forEach(statusOption => {
                        const option = document.createElement('option');
                        option.value = statusOption.value;
                        option.textContent = statusOption.text;
                        statusFilter.appendChild(option);
                    });

                    statusFilter.value = status; // Set the selected value to current status
                    statusFilter.addEventListener('change', function () {
                        fetchLeaveRequests(this.value); // Fetch data based on selected status
                    });

                    headerContainer.appendChild(statusFilter);
                    employeeDetails.appendChild(headerContainer);

                    // Create the table
                    const table = document.createElement('table');
                    table.classList.add('table', 'table-striped');

                    const thead = document.createElement('thead');
                    const headerRow = document.createElement('tr');
                    const headers = ['Leave Type', 'From Date', 'To Date', 'Date of Application', 'Total Days', 'Status', 'Actions'];
                    headers.forEach(headerText => {
                        const th = document.createElement('th');
                        th.textContent = headerText;
                        headerRow.appendChild(th);
                    });
                    thead.appendChild(headerRow);
                    table.appendChild(thead);

                    const tbody = document.createElement('tbody');
                    data.forEach(leaveRequest => {
                        const row = document.createElement('tr');

                        const leaveTypeCell = document.createElement('td');
                        leaveTypeCell.textContent = leaveRequest.leaveType;
                        row.appendChild(leaveTypeCell);

                        const fromDateCell = document.createElement('td');
                        fromDateCell.textContent = new Date(leaveRequest.fromDate).toLocaleDateString();
                        row.appendChild(fromDateCell);

                        const toDateCell = document.createElement('td');
                        toDateCell.textContent = new Date(leaveRequest.toDate).toLocaleDateString();
                        row.appendChild(toDateCell);

                        const dateOfApplicationCell = document.createElement('td');
                        dateOfApplicationCell.textContent = leaveRequest.dateOfApplication;
                        row.appendChild(dateOfApplicationCell);

                        const totalDaysCell = document.createElement('td');
                        totalDaysCell.textContent = leaveRequest.totalDays;
                        row.appendChild(totalDaysCell);

                        const statusCell = document.createElement('td');
                        statusCell.textContent = leaveRequest.leaveRequestStatus;
                        row.appendChild(statusCell);

                        const actionsCell = document.createElement('td');

                        if (leaveRequest.leaveRequestStatus === 'PENDING') {
                            const editButton = document.createElement('button');
                            editButton.textContent = 'Edit';
                            editButton.classList.add('btn', 'btn-warning', 'mr-2');
                            // Add edit functionality here
                            actionsCell.appendChild(editButton);

                            const cancelButton = document.createElement('button');
                            cancelButton.textContent = 'Cancel';
                            cancelButton.classList.add('btn', 'btn-danger', 'mr-2');

                            // Add event listener for the Cancel button
                            cancelButton.addEventListener('click', function () {
                                if (leaveRequest.leaveRequestId) {
                                    fetch(`http://localhost:8080/api/employee/leave?leaveRequestId=${leaveRequest.leaveRequestId}`, {
                                        method: 'POST',
                                        headers: {
                                            'Content-Type': 'application/json'
                                        },
                                        body: JSON.stringify({ leaveRequestId: leaveRequest.leaveRequestId })
                                    })
                                        .then(response => response.json())
                                        .then(result => {
                                            if (result) {
                                                alert('Leave request canceled successfully.');
                                                fetchLeaveRequests(statusFilter.value); // Refresh the list
                                            } else {
                                                alert('Failed to cancel leave request.');
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error:', error);
                                            alert('An error occurred while canceling the leave request.');
                                        });
                                } else {
                                    console.error('Error: leaveRequestId is undefined.');
                                    alert('Unable to cancel leave request. Leave request ID is missing.');
                                }
                            });

                            actionsCell.appendChild(cancelButton);
                        }

                        const detailsButton = document.createElement('button');
                        detailsButton.textContent = 'Details';
                        detailsButton.classList.add('btn', 'btn-info');
                        detailsButton.addEventListener('click', () => {
                            const modalTitle = document.getElementById('leaveDetailsModalLabel');
                            const modalBody = document.getElementById('leaveDetailsModalBody');

                            modalTitle.textContent = `My Leave Details`;
                            modalBody.innerHTML = `
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Type:</strong>
                                   ${leaveRequest.leaveType}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Leave Reason:</strong>
                                   ${leaveRequest.leaveReason}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">From Date:</strong>
                                   ${new Date(leaveRequest.fromDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">To Date:</strong>
                                   ${new Date(leaveRequest.toDate).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Application:</strong>
                                   ${new Date(leaveRequest.dateOfApplication).toLocaleDateString()}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Total Days:</strong>
                                   ${leaveRequest.totalDays}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Status:</strong>
                                   ${leaveRequest.leaveRequestStatus}
                               </p>
                               <p style="margin-bottom: 10px;">
                                   <strong style="display: inline-block; width: 150px;">Date of Action:</strong>
                                   ${new Date(leaveRequest.dateOfApproved).toLocaleDateString()}
                               </p>
                           `;


                            const leaveDetailsModal = new bootstrap.Modal(document.getElementById('leaveDetailsModal'));
                            leaveDetailsModal.show();
                        });

                        actionsCell.appendChild(detailsButton);
                        row.appendChild(actionsCell);

                        tbody.appendChild(row);
                    });
                    table.appendChild(tbody);

                    employeeDetails.appendChild(table);
                })
                .catch(error => console.error('Error fetching leave requests:', error));
        }
    });
});