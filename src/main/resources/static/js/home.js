

function showInputRow() {


        const inputRow = document.getElementById("inputRow");

        // Kiểm tra xem dòng có đang ẩn hay không
        if (inputRow.style.display === "none" || inputRow.style.display === "") {
            inputRow.style.display = "table-row"; // Hiển thị dòng

            // Hiển thị cột giảng viên 1 và 2
            document.querySelectorAll(".firstAdvisorColumn").forEach(function(column) {
                column.style.display = '';
            });
            document.querySelectorAll(".secondAdvisorColumn").forEach(function(column) {
                column.style.display = '';
            });
            // Hiển thị cột nơi công tác 1 và 2
            document.querySelectorAll(".firstAdvisorWsColumn").forEach(function(column) {
                column.style.display = '';
            });
            document.querySelectorAll(".secondAdvisorWsColumn").forEach(function(column) {
                column.style.display = '';
            });

            // Ẩn cột giảng viên chung
            document.querySelectorAll(".advisorColumn").forEach(function(column) {
                column.style.display = 'none';
            });
            // Ẩn cột công tác chung
            document.querySelectorAll(".advisorWsColumn").forEach(function(column) {
                column.style.display = 'none';
            });
        } else {
            inputRow.style.display = "none"; // Ẩn dòng
            // Ẩn cột giảng viên 1 và 2
            document.querySelectorAll(".firstAdvisorColumn").forEach(function(column) {
                column.style.display = 'none';
            });
            document.querySelectorAll(".secondAdvisorColumn").forEach(function(column) {
                column.style.display = 'none';
            });
            document.querySelectorAll(".firstAdvisorWsColumn").forEach(function(column) {
                column.style.display = 'none';
            });
            document.querySelectorAll(".secondAdvisorWsColumn").forEach(function(column) {
                column.style.display = 'none';
            });
            // Hiển thị cột giảng viên chung
            document.querySelectorAll(".advisorColumn").forEach(function(column) {
                column.style.display = '';
            });
            document.querySelectorAll(".advisorWsColumn").forEach(function(column) {
                column.style.display = '';
            });
        }


}


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


function exportTableToCSV(filename) {
    var csv = [];
    var rows = document.querySelectorAll("table tr");

    for (var i = 0; i < rows.length; i++) {
        var row = [], cols = rows[i].querySelectorAll("td, th");

        for (var j = 0; j < cols.length-1; j++) {
            // Kiểm tra xem cột có đang được hiển thị không
            if (cols[j].style.display !== 'none') {
                row.push('"' + cols[j].innerText + '"');
            }
        }

        if (row.length > 0) {
            csv.push(row.join(","));
        }
    }

    // Nếu không có dữ liệu để xuất, thoát khỏi hàm
    if (csv.length === 0) {
        return;
    }

    // Tiến hành tải xuống CSV
    downloadCSV(csv.join("\n"), filename);
}


function downloadCSV(csv, filename) {
    var csvFile;
    var downloadLink;

    // CSV file
    csvFile = new Blob([csv], {type: "text/csv"});

    // Download link
    downloadLink = document.createElement("a");

    // File name
    downloadLink.download = filename;

    // Create a link to the file
    downloadLink.href = window.URL.createObjectURL(csvFile);

    // Hide download link
    downloadLink.style.display = "none";

    // Add the link to DOM
    document.body.appendChild(downloadLink);

    // Click download link
    downloadLink.click();
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
// listenEvent cho thẻ div NewsItem
document.addEventListener("DOMContentLoaded", function() {
    // Lấy tất cả các div với class NewsItem
    const newsItems = document.querySelectorAll(".NewsItem");

    // Thêm sự kiện click cho mỗi div
    newsItems.forEach(newsItem => {
        newsItem.addEventListener("click", function() {
            // Lấy giá trị ID từ thuộc tính data-id
            const newsID = this.getAttribute("data-id");
            // Chuyển hướng đến trang chi tiết tin tức
            window.location.href = `/home/news/${newsID}`;
        });
    });
});


$(document).ready(function () {

    $('table .edit').on('click', function (event) {

        event.preventDefault();

        var href = $(this).attr('href');
        $.get(href, function (student) {
            console.log(student)
            $('#hiddenId').val(student.id);
            $('#IdEdit').val(student.studentID);
            $('#NameEdit').val(student.name);
            $('#BirthEdit').val(student.dateOfBirth);
            $('#DTBCEdit').val(student.dtbc);
            $('#TopicEdit').val(student.thesistopics);
            $('#NewTopicEdit').val(student.newTopics);
            $('#lecturerEdit').val(student.namelecturer);
            $('#secondLecturerEdit').val(student.namesecondlecturer);
            $('#UniversityEdit').val(student.university);
            $('#UniversitySeEdit').val(student.secondLecturerWorkSpace);
            $('#lecturerReviewEdit').val(student.lecturerReviewer);
            $('#lecturerReviewWorkSpaceEdit').val(student.lecturerReviewerWorkplace);
            $('#secondLecturerReviewEdit').val(student.secondLecturerReviewer);
            $('#secondLecturerReviewWorkSpaceEdit').val(student.secondLecturerReviewerWorkplace);
            console.log(student.filePath)
            document.getElementById('existingFilePath').textContent = student.filePath;
            $('#StatusEdit').val(student.status);
        })
        $('#editModal').modal('show');
    });
});

//
document.addEventListener('DOMContentLoaded', function() {
    var fileUpload = document.getElementById('fileUpload');
    if (fileUpload) {
        fileUpload.addEventListener('change', function(e) {
            var file = e.target.files[0];
            var maxSize = 10 * 1024 * 1024; // 10MB

            if (file && file.size > maxSize) {
                alert('Dung lượng file không được vượt quá 10MB.');
                e.target.value = ''; // Reset lại trường file
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var fileExcel = document.getElementById('file');
    if (fileExcel) {
        fileExcel.addEventListener('change', function(e) {
            var file = e.target.files[0];
            var maxSize = 10 * 1024 * 1024; // 10MB

            if (file && file.size > maxSize) {
                alert('Dung lượng file không được vượt quá 10MB.');
                e.target.value = ''; // Reset lại trường file
            }
        });
    }
});


document.addEventListener("DOMContentLoaded", function() {
    // Đảm bảo hàm submitForm khả dụng khi DOM được tải đầy đủ
    const saveButton = document.getElementById("saveButton");
    saveButton.addEventListener("click", submitForm);
});

// Hàm này sẽ trả về giá trị của phần tử nếu nó tồn tại, hoặc null nếu không.
const getElementValue = (id) => {
    const element = document.getElementById(id);
    return element ? element.value : null;
};


function submitForm() {

    const updateStudentForm = document.getElementById("updateStudentForm");
    const formData = new FormData(updateStudentForm);

    const id = document.getElementById("hiddenId").value; // Lấy ID từ trường ẩn
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const academicYear = document.getElementById('academicYear').value;
    const semester = document.getElementById('semester').value;
    const url = `/students/${id}?academicYear=${academicYear}&semester=${semester}`;
    fetch(url, { // Sử dụng studentId như một phần của URL
        method: 'PUT',
        headers: {
            'X-CSRF-Token': csrfToken // Đính kèm token CSRF ở đây
        },
        body: formData // FormData bao gồm cả dữ liệu và tập tin
    })
        .then(response => response.json())
        .then(data => {
            if(data.status === 'success') {
                showSuccessModal();
                // location.reload();
            } else {
                showfailureModal();
            }
        })
        .catch(error => {
            console.error('There was an error!', error);
        });
}
function showSuccessModal() {
    $('#successEditModal').modal('show');

    $('#successEditModal').on('hidden.bs.modal', function (e) {
        location.reload();
    });
}
function showfailureModal() {
    $('#failureModal').modal('show');

    $('#failureModal').on('hidden.bs.modal', function (e) {
        location.reload();
    });
}


function confirmAndSubmit() {
    var isConfirmed = confirm("Danh sách sau khi xác nhận sẽ không thể thay đổi được nữa. Bạn có chắc chắn muốn tiếp tục không?");
    if (isConfirmed) {
        // Gửi form nếu người dùng xác nhận
        return true;
    } else {
        // Hủy gửi form nếu người dùng không xác nhận
        return false;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    // Mã để kiểm tra xem người dùng đã upload file lần đầu chưa
    // Ví dụ, sử dụng localStorage cho mục đích demo
    // $('#uploadModal').modal('show');
    if (!localStorage.getItem("hasShownUploadModal")) {
        // Hiển thị modal
        $('#uploadModal').modal('show');
        // Thiết lập flag trong localStorage
        localStorage.setItem("hasShownUploadModal", true);
    }
});

document.addEventListener("DOMContentLoaded", function() {
    // Lắng nghe sự kiện click trên ảnh
    document.querySelectorAll('.clickable-image').forEach(image => {
        image.addEventListener('click', function() {
            document.getElementById('modalImage').src = this.src; // Cập nhật src của ảnh trong modal
            $('#imageModal').modal('show'); // Hiển thị modal
        });
    });
});

// show recomment list lecturer when input data
$(document).ready(function() {
    var isLecturerSelected = false;
    var isSecondLecturerSelected = false;

    // Xử lý nhập liệu và hiển thị danh sách gợi ý cho instructorsInput
    $('#instructorsInput').keyup(function() {
        isLecturerSelected = false;
        var query = $(this).val();

        if (query != '') {
            $.ajax({
                url: "/students/suggestions",
                method: "GET",
                data: {query: query},
                success: function(data) {
                    $('#lecturerList').fadeIn();
                    $('#lecturerList').html(data);
                }
            });
        } else {
            $('#lecturerList').fadeOut();
        }
    });

    // Xử lý nhập liệu và hiển thị danh sách gợi ý cho secondInstructorsInput
    $('#secondInstructorsInput').keyup(function() {
        isSecondLecturerSelected = false;
        var query = $(this).val();

        if (query != '') {
            $.ajax({
                url: "/students/suggestions",
                method: "GET",
                data: {query: query},
                success: function(data) {
                    $('#secondLecturerList').fadeIn();
                    $('#secondLecturerList').html(data);
                }
            });
        } else {
            $('#secondLecturerList').fadeOut();
        }
    });

    // Xử lý chọn mục từ danh sách gợi ý
    $(document).on('click', '.suggestions-list li', function() {
        var inputTarget = $(this).closest('.suggestions-list').data('input-target');
        $(inputTarget).val($(this).text());
        $(this).closest('.suggestions-list').fadeOut();

        if(inputTarget === '#instructorsInput') {
            isLecturerSelected = true;
        } else if(inputTarget === '#secondInstructorsInput') {
            isSecondLecturerSelected = true;
        }
    });

    // Kiểm tra trước khi chuyển sang trường nhập liệu khác
    $('input').on('focus', function() {
        if($(this).attr('id') === 'instructorsInput' && !isLecturerSelected && $('#instructorsInput').val() !== '') {
            alert('Vui lòng chọn giảng viên trong danh sách gợi ý hoặc xóa nhập liệu');
            $('#instructorsInput').focus();
        } else if($(this).attr('id') === 'secondInstructorsInput' && !isSecondLecturerSelected && $('#secondInstructorsInput').val() !== '') {
            alert('Vui lòng chọn giảng viên trong danh sách gợi ý hoặc xóa nhập liệu');
            $('#secondInstructorsInput').focus();
        }
    });
});
//js cho toast bootstrap
document.addEventListener('DOMContentLoaded', function () {
    var toastElList = [].slice.call(document.querySelectorAll('.toast'))
    var toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl)
    })
    toastList.forEach(toast => toast.show())
});




















