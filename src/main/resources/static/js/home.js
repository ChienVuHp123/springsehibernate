

function showInputRow() {
    // const rows = document.querySelectorAll("#studentsTable tbody tr");
    // document.querySelector("#inputRow td:first-child").textContent = rows.length;

        const inputRow = document.getElementById("inputRow");

        // Kiểm tra xem dòng có đang ẩn hay không
        if (inputRow.style.display === "none" || inputRow.style.display === "") {
            inputRow.style.display = "table-row"; // Hiển thị dòng
        } else {
            inputRow.style.display = "none"; // Ẩn dòng
        }


}

// function addStudent() {
//     const ID = document.getElementById("IDInput").value;
//     const name = document.getElementById("nameInput").value;
//     const birth = document.getElementById("birthInput").value;
//     const DTBC = document.getElementById("DTBCInput").value;
//     const topic = document.getElementById("topicInput").value;
//     const instructors = document.getElementById("instructorsInput").value;
//     const university = document.getElementById("universityInput").value;
//     const lecturerReview = document.getElementById("lecturerReviewInput").value;
//     const lecturerReviewWorkSpace = document.getElementById("lecturerReviewWorkSpaceInput").value;
//
//     // Create new row and cells
//     const newRow = document.createElement("tr");
//     const IDCell = document.createElement("td");
//     const nameCell = document.createElement("td");
//     const birthCell = document.createElement("td");
//     const DTBCCell = document.createElement("td");
//     const topicCell = document.createElement("td");
//     const instructorsCell = document.createElement("td");
//     const universityCell = document.createElement("td");
//     const lecturerReviewCell = document.createElement("td");
//     const lecturerReviewWorkSpaceCell = document.createElement("td");
//
//     // Set the text for the new cells
//     // numberCell.textContent = document.querySelector("#inputRow td:first-child").textContent;
//     IDCell.textContent = ID;
//     nameCell.textContent = name;
//     birthCell.textContent = birth;
//     DTBCCell.textContent = DTBC;
//     topicCell.textContent = topic;
//     instructorsCell.textContent = instructors;
//     universityCell.textContent = university;
//     lecturerReviewCell.textContent = lecturerReview;
//     lecturerReviewWorkSpaceCell.textContent = lecturerReviewWorkSpace;
//
//     // Append the cells to the new row
//     newRow.appendChild(IDCell);
//     newRow.appendChild(nameCell);
//     newRow.appendChild(birthCell);
//     newRow.appendChild(DTBCCell);
//     newRow.appendChild(topicCell);
//     newRow.appendChild(instructorsCell);
//     newRow.appendChild(universityCell);
//     newRow.appendChild(lecturerReviewCell);
//     newRow.appendChild(lecturerReviewWorkSpaceCell);
//
//     // Insert the new row before the input row
//     const inputRow = document.getElementById("inputRow");
//     document.getElementById("studentsTable").tBodies[0].insertBefore(newRow, inputRow);
//
//     // Clear input fields and hide row
//     document.getElementById("IDInput").value = "";
//     document.getElementById("nameInput").value = "";
//     document.getElementById("birthInput").value = "";
//     document.getElementById("DTBCInput").value = "";
//     document.getElementById("topicInput").value = "";
//     document.getElementById("instructorsInput").value = "";
//     document.getElementById("universityInput").value = "";
//     document.getElementById("lecturerReviewInput").value = "";
//     document.getElementById("lecturerReviewWorkSpaceInput").value = "";
//     document.getElementById("inputRow").style.display = "none";
//
// }

function deleteRow(element) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    // Tìm thẻ <tr> chứa thẻ <a> bạn nhấn
    const rowToDelete = element.closest("tr");

    const studentID = element.getAttribute("data-id");
    if (confirm("Bạn có chắc chắn muốn xóa học sinh này không?")) {
        fetch(`/students/${studentID}`, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken
            }
        })
            .then(response => {
                if (response.ok) {
                    // Xóa dòng từ bảng hoặc làm mới trang
                    location.reload();
                } else {
                    // alert('Lỗi khi xóa học sinh.');
                }
            });
        rowToDelete.remove();
    }

    // Cập nhật lại số thứ tự trong cột TT
    // updateOrderNumbers();
}

// function editStudent(button) {
//     const studentId = button.getAttribute("th:data-id");
//     const modal = document.getElementById("editStudentModal");
//     const studentNameInput = document.getElementById("studentName");
//     const studentIdInput = document.getElementById("studentId");
//
//     // Gửi yêu cầu lấy thông tin sinh viên theo studentId
//     // Sau khi nhận được thông tin, điền vào form và mở modal
//     // Đảm bảo bạn đã lấy đúng thông tin cần thiết từ phản hồi
//     // Ví dụ: studentNameInput.value = student.name;
//
//     // Mở modal
//     modal.style.display = "block";
// }
//
// function closeModal() {
//     const modal = document.getElementById("editStudentModal");
//     modal.style.display = "none";
// }
// function updateOrderNumbers() {
//     const rows = document.querySelectorAll("#studentsTable tbody tr:not(#inputRow)");
//     for (let i = 0; i < rows.length; i++) {
//         rows[i].querySelector("td:first-child").textContent = i + 1;
//     }
// }

function downloadCSV(csv, filename) {
    var csvFile;
    var downloadLink;

    // CSV FILE
    csvFile = new Blob([csv], {type: "text/csv"});

    // Download link
    downloadLink = document.createElement("a");

    // File name
    downloadLink.download = filename;

    // We have to create a link to the file
    downloadLink.href = window.URL.createObjectURL(csvFile);

    // Make sure that the link is not displayed
    downloadLink.style.display = "none";

    // Add the link to your DOM
    document.body.appendChild(downloadLink);

    // Lanch the download
    downloadLink.click();
}

function exportTableToCSV(filename) {
    var csv = [];
    var rows = document.querySelectorAll("table tr");

    for (var i = 0; i < rows.length; i++) {
        var row = [], cols = rows[i].querySelectorAll("td, th");

        for (var j = 0; j < cols.length-1; j++)
            row.push(cols[j].innerText);

        csv.push(row.join(","));
    }

    // Download CSV
    downloadCSV(csv.join("\n"), filename);
}

function tableToJson(table) {
    const rows = Array.from(table.querySelectorAll('tr'));
    const headerRow = rows.shift();
    const headers = Array.from(headerRow.querySelectorAll('th')).map(th => th.innerText);

    const data = rows.map(row => {
        const rowData = {};
        const cells = Array.from(row.querySelectorAll('td'));
        cells.forEach((cell, index) => {
            rowData[headers[index]] = cell.innerText;
        });
        return rowData;
    });

    return data;
}

function sendData() {
    const table = document.getElementById('studentsTable');
    const data = tableToJson(table);

    // Sử dụng URL của Mock Server bạn cung cấp
    const postmanURL = "https://bf6d2f18-becf-409c-81a5-e1c63fab6652.mock.pstmn.io/send-data";

    fetch(postmanURL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }).then(response => {
        if (response.ok) {
            alert('Dữ liệu đã được gửi thành công!');
        } else {
            alert('Dữ liệu gui that bai');
        }
    }).catch(error => {
        console.error('There was an error!', error);
    });

}

document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll("#nav li a").forEach(function(link) {
        if(window.location.href.includes(link.getAttribute("href"))) {
            document.getElementById("currentNav").innerText = link.innerText;
        }
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const divs = document.querySelectorAll(".GiangVienDoAn");

    divs.forEach(div => {
        div.addEventListener("click", function() {
            const lecturerID = this.getAttribute("data-id");
            window.location.href = `/bo-mon/list/${lecturerID}`;
        });
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const divs = document.querySelectorAll(".BoMonList");

    divs.forEach(div => {
        div.addEventListener("click", function() {
            const departmentID = this.getAttribute("data-id");
            window.location.href = `/khoa/list/${departmentID}`;
        });
    });
});


$(document).ready(function () {

    $('table .edit').on('click', function (event) {

        event.preventDefault();

        var href = $(this).attr('href');
        $.get(href, function (student) {
            console.log(student)
            $('#IdEdit').val(student.id);   // assuming your student object has 'id' in lowercase
            $('#NameEdit').val(student.name);
            $('#BirthEdit').val(student.dateOfBirth);
            $('#DTBCEdit').val(student.dtbc);
            $('#TopicEdit').val(student.thesistopics);
            $('#lecturerEdit').val(student.namelecturer);
            $('#UniversityEdit').val(student.university);
            $('#lecturerReviewEdit').val(student.lecturerReviewer);
            $('#lecturerReviewWorkSpaceEdit').val(student.lecturerReviewerWorkplace);

        })
            // .fail(function() {
            //     alert("Student not found.");
            // })
            // .always(function() {
            //     $('#editModal').modal('show');
            // });
        $('#editModal').modal('show');
    });
});

// function checkAndHideButton() {
//     var showColumns = /*[# th:utext="${showColumns}"]*/false/*[/]*/; // Lấy giá trị từ Thymeleaf
//
//     if (showColumns) {
//         document.getElementById('sendButton').style.display = 'block'; // Hiển thị nút
//     } else {
//         document.getElementById('sendButton').style.display = 'none'; // Ẩn nút
//     }
// }
//
// document.addEventListener("DOMContentLoaded", function() {
//     checkAndHideButton();
// });










