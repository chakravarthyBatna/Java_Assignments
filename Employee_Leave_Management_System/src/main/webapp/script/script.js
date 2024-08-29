document.addEventListener("DOMContentLoaded", function () {

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
    const employeeDetails = document.getElementById("employee-details");

    // Function to fetch and display employee data
    function fetchAndDisplayEmployeeData() {
        fetch("http://localhost:8080/api/login")
            .then(response => response.json())
            .then(data => {
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

                employeeDetails.innerHTML = detailsHtml;
            })
            .catch(error => {
                console.error("Error fetching employee data:", error);
            });
    }

    // Add event listeners to profile pic and profile name
    profilePic.addEventListener("click", fetchAndDisplayEmployeeData);
    profileName.addEventListener("click", fetchAndDisplayEmployeeData);
// Event listener for "My Team Leaves" button
// Event listener for "My Team Leaves" button
document.getElementById('my-team-leaves').addEventListener('click', function () {
    fetchTeamLeaveRequests('ALL'); // Initial fetch with ALL status

    function fetchTeamLeaveRequests(status) {
        fetch(`http://localhost:8080/api/employee/my-team-leave?status=${status}`)
            .then(response => response.json())
            .then(data => {
                const employeeDetailsDiv = document.getElementById('employee-details');
                employeeDetailsDiv.innerHTML = ''; // Clear previous content

                // Create a container for the title, button, and status filter
                const headerContainer = document.createElement('div');
                headerContainer.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-3');

                // Create and add the title
                const title = document.createElement('h3');
                title.textContent = 'My Team Leaves';
                title.classList.add('text-center', 'w-100', 'font-weight-bold');
                headerContainer.appendChild(title);

                // Create and add the "Apply for Leave" button
                const applyButton = document.createElement('button');
                applyButton.textContent = 'Apply for Leave';
                applyButton.classList.add('btn', 'btn-primary','apply-btn');
                applyButton.style.float = 'right';
               applyButton.addEventListener('click', function () {
                   // Fetch user information first
                   fetch('http://localhost:8080/api/login')
                       .then(response => response.json())
                       .then(user => {
                           if (user.role === 'ADMIN') {
                               // Show prompt message if the user is an ADMIN
                               alert("You're an admin, you don't need any leave.");
                           } else {
                               // Fetch leave types if the user is not an ADMIN
                               fetch('http://localhost:8080/api/leavetypes')
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
                statusFilter.classList.add('form-select', 'w-auto', 'mr-2', 'get-leaves-dropdown');
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
                    if (statusOption.value === status) {
                        option.selected = true;
                    }
                    statusFilter.appendChild(option);
                });

                statusFilter.addEventListener('change', function () {
                    fetchTeamLeaveRequests(this.value); // Fetch data based on selected status
                });

                headerContainer.appendChild(statusFilter);
                employeeDetailsDiv.appendChild(headerContainer);

                // Create the table
                const table = document.createElement('table');
                table.classList.add('table', 'table-striped');

                const thead = document.createElement('thead');
                const headerRow = document.createElement('tr');
                const headers = ['Employee Name', 'Leave Type', 'From Date', 'To Date', 'Status', 'Actions'];
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
                                        document.getElementById('my-team-leaves').click();
                                    }
                                })
                                .catch(error => console.error('Error approving leave request:', error));
                        });

                        const rejectButton = document.createElement('button');
                        rejectButton.textContent = 'Reject';
                        rejectButton.classList.add('btn', 'btn-danger','reject-btn');
                        rejectButton.addEventListener('click', () => {
                            fetch(`http://localhost:8080/api/employee/my-team-leave?approveOrReject=REJECTED&leaveRequestId=${leaveRequest.leaveRequestId}`, {
                                method: 'POST'
                            })
                                .then(response => response.json())
                                .then(result => {
                                    if (result) {
                                        // Refresh the data
                                        document.getElementById('my-team-leaves').click();
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
                        // Update and show the modal with the correct details
                        const modalTitle = document.getElementById('leaveDetailsModalLabel');
                        const modalBody = document.getElementById('leaveDetailsModalBody');

                        modalTitle.textContent = `Leave Details for ${leaveRequest.empName}`;
                        modalBody.innerHTML = `
                          <p><strong>Employee Name:</strong> ${leaveRequest.empName}</p>
                          <p><strong>Leave Type:</strong> ${leaveRequest.leaveType}</p>
                          <p><strong>Leave Reason:</strong> ${leaveRequest.leaveReason}</p>
                          <p><strong>From Date:</strong> ${new Date(leaveRequest.fromDate).toLocaleDateString()}</p>
                          <p><strong>To Date:</strong> ${new Date(leaveRequest.toDate).toLocaleDateString()}</p>
                          <p><strong>Date Of Application:</strong> ${leaveRequest.dateOfApplication}</p>
                          <p><strong>Status:</strong> ${leaveRequest.leaveRequestStatus}</p>
                      `;

                        // Show the modal
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
});



    // Handle the form submission for applying for leave
    document.getElementById('applyLeaveForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const empId = 1; // Replace with the actual employee ID
        const leaveTypeId = document.getElementById('leaveTypeSelect').value;
        const leaveReason = document.getElementById('leaveReason').value;
        const fromDate = document.getElementById('fromDate').value;
        const toDate = document.getElementById('toDate').value;
        const dateOfApplication = new Date().toISOString().split('T')[0]; // Current date
        const leaveRequestStatus = 'PENDING';

        const leaveRequest = {
            empId,
            leaveTypeId,
            leaveReason,
            fromDate,
            toDate,
            dateOfApplication,
            leaveRequestStatus
        };

        fetch('http://localhost:8080/api/employees/leave', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(leaveRequest)
        })
            .then(response => response.json())
            .then(result => {
                if (result) {
                    // Hide the modal and refresh the leave data
                    const applyLeaveModal = bootstrap.Modal.getInstance(document.getElementById('applyLeaveModal'));
                    applyLeaveModal.hide();
                    document.getElementById('my-leaves').click();
                }
            })
            .catch(error => console.error('Error applying for leave:', error));
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

    // Append modal to the body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
   // My Leaves Details
  document.getElementById('my-leaves').addEventListener('click', function () {
      fetchLeaveRequests('ALL'); // Initial fetch with ALL status

      function fetchLeaveRequests(status) {
          fetch(`http://localhost:8080/api/employees/leave?status=${status}`)
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
                           fetch('http://localhost:8080/api/leavetypes')
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
                  const headers = ['Leave Type', 'From Date', 'To Date', 'Date of Application', 'Status', 'Actions'];
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
                                  fetch(`http://localhost:8080/api/employees/leave?leaveRequestId=${leaveRequest.leaveRequestId}`, {
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
                              <p><strong>Leave Type:</strong> ${leaveRequest.leaveType}</p>
                              <p><strong>Leave Reason:</strong> ${leaveRequest.leaveReason}</p>
                              <p><strong>From Date:</strong> ${new Date(leaveRequest.fromDate).toLocaleDateString()}</p>
                              <p><strong>To Date:</strong> ${new Date(leaveRequest.toDate).toLocaleDateString()}</p>
                              <p><strong>Date Of Application:</strong> ${leaveRequest.dateOfApplication}</p>
                              <p><strong>Status:</strong> ${leaveRequest.leaveRequestStatus}</p>
                              <p><strong>Date Of Approval:</strong> ${leaveRequest.dateOfApproved ? new Date(leaveRequest.dateOfApproved).toLocaleDateString() : 'N/A'}</p>
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